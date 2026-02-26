package ch.gryphus.chainvault.service;

import ch.gryphus.chainvault.config.SftpTargetConfig;
import ch.gryphus.chainvault.domain.*;
import ch.gryphus.chainvault.utils.HashUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * The type Migration service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {

    private final RestClient restClient;
    private final SftpRemoteFileTemplate sftpRemoteFileTemplate;
    private final SftpTargetConfig sftpTargetConfig;
    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;
    private final Tika tika;

    /**
     * Extract and hash map.
     *
     * @param docId the doc id
     * @return the map
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public Map<String, Object> extractAndHash(String docId) throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        byte[] payload;

        var ctx = new MigrationContext();
        ctx.setDocId(docId);
        map.put("ctx", ctx);

        var meta = restClient.get()
                .uri("/documents/{id}", docId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        throw new MigrationServiceException(docId, response.getStatusCode(), response.getHeaders());
                    } else {
                        return response.bodyTo(SourceMetadata.class);
                    }
                });
        if (meta != null) {
            map.put("meta", meta);
            if (meta.getPayloadUrl() != null) {
                payload = restClient.get()
                        .uri(meta.getPayloadUrl())
                        .accept(MediaType.APPLICATION_OCTET_STREAM)
                        .exchange((request, response) -> {
                            if (response.getStatusCode().is4xxClientError()) {
                                throw new MigrationServiceException(docId, response.getStatusCode(), response.getHeaders());
                            } else {
                                return response.bodyTo(byte[].class);
                            }
                        });
                ctx.setPayloadHash(HashUtils.sha256(payload));
                map.put("payload", payload);
            }
        }
        return map;
    }

    /**
     * Sign tiff pages list.
     *
     * @param payload the payload
     * @param ctx     the ctx
     * @return the list
     * @throws IOException              the io exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public List<TiffPage> signTiffPages(byte[] payload, MigrationContext ctx) throws IOException, NoSuchAlgorithmException {
        List<TiffPage> pages = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(payload))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".tif") || entry.getName().toLowerCase().endsWith(".tiff")) {
                    byte[] data = zis.readAllBytes();

                    String pageHash = HashUtils.sha256(data);
                    ctx.addPageHash(entry.getName(), pageHash);
                    pages.add(new TiffPage(entry.getName(), data));
                }
            }
        }
        return pages;
    }

    /**
     * Create chain zip path.
     *
     * @param docId          the doc id
     * @param pages          the pages
     * @param sourceMetadata the source metadata
     * @param ctx            the ctx
     * @return the path
     * @throws IOException              the io exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public Path createChainZip(String docId, List<TiffPage> pages, SourceMetadata sourceMetadata, MigrationContext ctx)
            throws IOException, NoSuchAlgorithmException {

        Path zipPath = Files.createTempFile("%s_chain".formatted(docId), ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {

            // 1. Add all TIFF pages
            for (int i = 0; i < pages.size(); i++) {
                TiffPage page = pages.get(i);
                String entryName = String.format("page-%03d_%s", i + 1, page.name());

                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(page.data());
                zos.closeEntry();
            }

            // 2. Add manifest.json
            Map<String, Object> manifest = new LinkedHashMap<>();
            manifest.put("docId", docId);
            manifest.put("timestamp", Instant.now().toString());
            manifest.put("pageCount", pages.size());
            manifest.put("pageHashes", ctx.getPageHashes());
            manifest.put("payloadHash", ctx.getPayloadHash());

            // optional: snapshot of source metadata
            if (sourceMetadata != null) {
                manifest.put("sourceMetadata", Map.of(
                        "docId", sourceMetadata.getDocId(),
                        "title", sourceMetadata.getTitle(),
                        "creationDate", sourceMetadata.getCreationDate(),
                        "clientId", sourceMetadata.getClientId()
                ));
            }

            String manifestJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(manifest);

            zos.putNextEntry(new ZipEntry("manifest.json"));
            zos.write(manifestJson.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        // Optional: log final hash for audit trail
        String zipHash = HashUtils.sha256(zipPath);
        log.info("Chain ZIP created: {} | hash = {}", zipPath.getFileName(), zipHash);

        return zipPath;
    }

    /**
     * Upload to sftp.
     *
     * @param ctx     the ctx
     * @param docId   the doc id
     * @param xml     the xml
     * @param zipPath the zip path
     * @param pdfPath the pdf path
     */
    public void uploadToSftp(MigrationContext ctx, String docId, String xml, Path zipPath, Path pdfPath) {
        String folder = "%s/%s".formatted(sftpTargetConfig.getRemoteDirectory(), docId);
        sftpRemoteFileTemplate.execute(s -> {
            if (!s.exists(folder)) {
                s.mkdir(folder);
            }
            s.write(Files.newInputStream(zipPath.toFile().toPath()), "%s/%s_chain.zip".formatted(folder, docId));
            s.write(Files.newInputStream(pdfPath.toFile().toPath()), "%s/%s.pdf".formatted(folder, docId));
            s.write(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), "%s/%s_meta.xml".formatted(folder, docId));
            return null;
        });
        log.info("Done {} | zipPath={} | pdf={}", docId, ctx.getZipHash(), ctx.getPdfHash());
    }

    /**
     * Merge tiff to pdf path.
     *
     * @param pages the pages
     * @param docId the doc id
     * @return the path
     * @throws IOException the io exception
     */
    public Path mergeTiffToPdf(List<TiffPage> pages, String docId) throws IOException {
        Path pdf = Path.of("/tmp/" + docId + ".pdf");
        try (var doc = new PDDocument()) {
            for (var page : pages) {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(page.data()));
                try {
                    var pdImage = LosslessFactory.createFromImage(doc, img);
                    var pdPage = new PDPage(new PDRectangle(img.getWidth(), img.getHeight()));
                    doc.addPage(pdPage);

                    try (var cs = new PDPageContentStream(doc, pdPage)) {
                        cs.drawImage(pdImage, 0, 0);
                    }
                } catch (Exception ignored) {
                    log.error("Failed to merge PDF image to pdf: {}", docId);
                }
            }
            doc.save(pdf.toFile());
        }
        return pdf;
    }

    /**
     * Build xml archival metadata.
     *
     * @param sourceMetadata the source metadata
     * @param ctx            the ctx
     * @return the archival metadata
     */
    public ArchivalMetadata buildXml(SourceMetadata sourceMetadata, MigrationContext ctx) {
        ArchivalMetadata metadata = new ArchivalMetadata();

        // Core document info
        metadata.setDocumentId(ctx.getDocId());
        metadata.setTitle(sourceMetadata.getTitle() != null ? sourceMetadata.getTitle() : "Untitled Document");
        metadata.setCreationDate(sourceMetadata.getCreationDate());
        metadata.setClientId(sourceMetadata.getClientId());
        metadata.setDocumentType(sourceMetadata.getDocumentType());
        metadata.setPageCount(ctx.getPageHashes().size());

        // Integrity / chain of custody
        metadata.setPayloadHash(ctx.getPayloadHash());
        metadata.setZipHash(ctx.getZipHash());
        metadata.setPdfHash(ctx.getPdfHash());

        // Migration provenance
        MigrationProvenance provenance = new MigrationProvenance();
        provenance.setMigrationTimestamp(Instant.now().toString());
        provenance.setToolVersion("1.0.0");           // or read from pom.properties / build info
        provenance.setOperator("migration-service");  // or System.getProperty("user.name")
        provenance.setPageHashes(ctx.getPageHashes()); // Map<String, String> filename → hash

        metadata.setProvenance(provenance);

        metadata.setCustomFields(Map.of("sourceSystem", "legacy-archive-v1"));
        return metadata;
    }

    /**
     * Unzip tiff pages list.
     *
     * @param zipBytes the zip bytes
     * @return the list
     * @throws IOException the io exception
     */
    public List<TiffPage> unzipTiffPages(byte[] zipBytes) throws IOException {
        var pages = new ArrayList<TiffPage>();

        try (var zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                String nameLower = entry.getName().toLowerCase();
                if (nameLower.endsWith(".tif") || nameLower.endsWith(".tiff")) {
                    byte[] data = zis.readAllBytes();
                    pages.add(new TiffPage(entry.getName(), data));
                }
            }
        }

        if (pages.isEmpty()) {
            throw new IllegalStateException("No TIFF pages found in ZIP");
        }

        return pages;
    }


    /**
     * Transform metadata to xml string.
     *
     * @param meta the meta
     * @param ctx  the ctx
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    public String transformMetadataToXml(SourceMetadata meta, MigrationContext ctx) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(buildXml(Objects.requireNonNull(meta), ctx));
    }

    /**
     * Gets detected mime type.
     *
     * @param in the in
     * @return the detected mime type
     * @throws IOException the io exception
     */
    public String getDetectedMimeType(InputStream in) throws IOException {
        Tika tika = new Tika();
        return tika.detect(in);
    }
}
