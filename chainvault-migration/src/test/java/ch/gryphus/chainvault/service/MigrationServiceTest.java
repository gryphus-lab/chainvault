package ch.gryphus.chainvault.service;

import ch.gryphus.chainvault.config.SftpTargetConfig;
import ch.gryphus.chainvault.domain.ArchivalMetadata;
import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.domain.SourceMetadata;
import ch.gryphus.chainvault.domain.TiffPage;
import ch.gryphus.chainvault.utils.HashUtils;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.input.BrokenInputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.integration.file.remote.SessionCallback;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.web.client.RestClient;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * The type Migration service test.
 */
@ExtendWith(MockitoExtension.class)
class MigrationServiceTest {

    @Mock
    private RestClient mockRestClient;
    @Mock
    private RestClient.RequestHeadersUriSpec mockRwquestSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @Mock
    private SftpRemoteFileTemplate mockSftpRemoteFileTemplate;
    @Mock
    private SftpTargetConfig mockSftpTargetConfig;
    @Mock
    private Tika mockTika;

    // Constructed manually in setup() to avoid Mockito type-based injection confusion
    private MigrationService migrationServiceUnderTest;
    private MigrationContext ctx;
    private SourceMetadata meta;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        migrationServiceUnderTest = new MigrationService(mockRestClient, mockSftpRemoteFileTemplate,
                mockSftpTargetConfig, new XmlMapper(), new ObjectMapper(), mockTika);

        migrationServiceUnderTest.setWorkingDirectory("/tmp");

        String docId = "DOC-TEST-001";
        ctx = new MigrationContext();
        ctx.setDocId(docId);
        ctx.setPayloadHash("payload-sha256-abc123");

        meta = new SourceMetadata();
        meta.setDocId(docId);
        meta.setTitle("Test Invoice 2026");
        meta.setCreationDate(Instant.now().toString());
        meta.setClientId("CHE-123.456.789");
        meta.setDocumentType("INVOICE");
        meta.setHash("sha256-abc123");
        meta.setAccountNo("ACC-123");
        meta.setPayloadUrl("/payload/12345.zip");
    }

    /**
     * Test extract and hash when documents exist.
     *
     * @throws Exception the exception
     */
    @Disabled
    @Test
    void testExtractAndHash_whenDocumentsExist() throws Exception {
        // Setup
        // Configure RestClient.get(...).


        // Make fluent chain return itself (common pattern)
        when(mockRestClient.get()).thenReturn(mockRwquestSpec);
        when(mockRwquestSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(mockRwquestSpec);
        when(mockRwquestSpec.retrieve()).thenReturn(responseSpec);

        // Run the test
        final Map<String, Object> result = migrationServiceUnderTest.extractAndHash("DOC-TEST-001");

        // Verify the results
        assertThat(result).isNotNull();
        assertThat(result.get("payloadHash")).hasToString("sha256-abc123");
    }

    /**
     * Test sign tiff page with valid payload zip.
     *
     * @throws Exception the exception
     */
    @Test
    void testSignTiffPage_withValidPayloadZip() throws Exception {
        // Setup
        byte[] payload = Files.readAllBytes(Path.of("src/test/resources/zips/invoice-001.zip"));

        // Run the test
        final List<TiffPage> result = migrationServiceUnderTest.signTiffPages(payload, ctx);

        // Verify the results
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().name()).isEqualTo(("sample1.tiff"));
        assertThat(HashUtils.sha256(result.getFirst().data()))
                .isEqualTo("b6d36032c5a5d291a5d67ccdfdf09a5a90dedb29c50d0206660e453f77ffb8c5");
    }

    /**
     * Test upload to sftp.
     */
    @Test
    void testUploadToSftp() {
        // Setup
        ctx.setDocId("docId");
        ctx.setPayloadHash("payloadHash");
        ctx.setZipHash("zipHash");
        ctx.setPdfHash("pdfHash");
        ctx.setPageHashes(Map.ofEntries(Map.entry("value", "value")));

        when(mockSftpTargetConfig.getRemoteDirectory()).thenReturn("result");

        // Run the test
        migrationServiceUnderTest.uploadToSftp(ctx, "docId", "xml", Path.of("filename.txt"),
                Path.of("filename.txt"));

        // Verify the results
        verify(mockSftpRemoteFileTemplate).execute(any(SessionCallback.class));
    }

    /**
     * Test merge tiff to pdf happy path.
     *
     * @throws Exception the exception
     */
    @Test
    void testMergeTiffToPdf_HappyPath() throws Exception {
        // Setup
        final List<TiffPage> pages = List.of(
                new TiffPage("sample1.tiff", Files.readAllBytes(Path.of("src/test/resources/tiffs/sample1.tiff"))),
                new TiffPage("sample2.tiff", Files.readAllBytes(Path.of("src/test/resources/tiffs/sample2.tiff")))
        );

        // Run the test
        final Path result = migrationServiceUnderTest.mergeTiffToPdf(pages, "docId");

        // Verify the results
        assertThat(result.toFile()).exists();
        byte[] resultBytes = Files.readAllBytes(result);
        assertThat(migrationServiceUnderTest.getDetectedMimeType(resultBytes)).isEqualTo("application/pdf");
    }

    /**
     * Test transform metadata to xml.
     *
     * @throws Exception the exception
     */
    @Test
    void testTransformMetadataToXml() throws Exception {
        // Setup
        meta.setDocId("docId");
        meta.setTitle("title");
        meta.setCreationDate("creationDate");
        meta.setClientId("clientId");
        meta.setDocumentType("documentType");
        meta.setPayloadUrl("payloadUrl");

        ctx.setDocId("docId");
        ctx.setPayloadHash("payloadHash");
        ctx.setZipHash("zipHash");
        ctx.setPdfHash("pdfHash");
        ctx.setPageHashes(Map.ofEntries(Map.entry("value", "value")));

        // Run the test
        final String result = migrationServiceUnderTest.transformMetadataToXml(meta, ctx);

        // Verify the results
        Diff diff = DiffBuilder.compare(Input.fromFile("src/test/resources/xmls/ArchivalMetadata.xml"))
                .withTest(result)
                // Ignore all nodes with 'Date' name
                .withNodeFilter(node -> !"migrationTimestamp".equals(node.getNodeName()))
                .build();

        assertThat(diff.hasDifferences()).isFalse();
    }

    /**
     * Test get detected mime type.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetDetectedMimeType() throws Exception {
        // Setup
        final InputStream in = new ByteArrayInputStream("content".getBytes());

        // Run the test
        final String result = migrationServiceUnderTest.getDetectedMimeType(in);

        // Verify the results
        assertThat(result).isEqualTo("text/plain");
    }

    /**
     * Test get detected mime type empty in.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetDetectedMimeType_EmptyIn() throws Exception {
        // Setup
        final InputStream in = InputStream.nullInputStream();

        // Run the test
        final String result = migrationServiceUnderTest.getDetectedMimeType(in);

        // Verify the results
        assertThat(result).isEqualTo("application/octet-stream");
    }

    /**
     * Test get detected mime type broken in.
     */
    @Test
    void testGetDetectedMimeType_BrokenIn() {
        // Setup
        final InputStream in = new BrokenInputStream();

        // Run the test
        assertThatThrownBy(() -> migrationServiceUnderTest.getDetectedMimeType(in)).isInstanceOf(IOException.class);
    }

    /**
     * Unzip tiff pages should extract and preserve order.
     *
     * @throws Exception the exception
     */
// ────────────────────────────────────────────────
    // unzipTiffPages
    // ────────────────────────────────────────────────
    @Test
    void unzipTiffPages_shouldExtractAndPreserveOrder() throws Exception {
        byte[] zip = createZipWithTiffs(List.of(
                "page-001.tif", "TIFF content 1",
                "page-002.tif", "TIFF content 2",
                "page-003.tif", "TIFF content 3"
        ));

        List<TiffPage> pages = migrationServiceUnderTest.unzipTiffPages(zip);

        assertThat(pages).hasSize(3);
        assertThat(pages.get(0).name()).isEqualTo("page-001.tif");
        assertThat(pages.get(1).name()).isEqualTo("page-002.tif");
        assertThat(new String(pages.get(0).data())).isEqualTo("TIFF content 1");
        assertThat(pages.get(2).name()).isEqualTo("page-003.tif");
    }

    /**
     * Unzip tiff pages should ignore non tiff files.
     *
     * @throws Exception the exception
     */
    @Test
    void unzipTiffPages_shouldIgnoreNonTiffFiles() throws Exception {
        byte[] zip = createZipWithTiffs(List.of(
                "page-001.tif", "TIFF1",
                "readme.txt", "ignore me",
                "page-002.tif", "TIFF2"
        ));

        List<TiffPage> pages = migrationServiceUnderTest.unzipTiffPages(zip);

        assertThat(pages).hasSize(2);
        assertThat(pages.get(1).name()).isEqualTo("page-002.tif");
    }

    /**
     * Unzip tiff pages should throw when no tiffs.
     *
     * @throws Exception the exception
     */
    @Test
    void unzipTiffPages_shouldThrowWhenNoTiffs() throws Exception {
        byte[] zip = createZipWithTiffs(List.of(
                "readme.txt", "no tiffs here"
        ));

        assertThatThrownBy(() -> migrationServiceUnderTest.unzipTiffPages(zip))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No TIFF pages found in ZIP");
    }

    /**
     * Create chain zip should produce valid zip with manifest.
     *
     * @throws Exception the exception
     */
// ────────────────────────────────────────────────
    // createChainZip
    // ────────────────────────────────────────────────
    @Test
    void createChainZip_shouldProduceValidZipWithManifest() throws Exception {
        // Arrange - ensure meta is non-null and has values
        assertThat(meta).isNotNull();
        assertThat(meta.getTitle()).isNotNull(); // fail fast if setup broken

        List<TiffPage> pages = List.of(
                new TiffPage("sample1.tiff", Files.readAllBytes(Path.of("src/test/resources/tiffs/sample1.tiff"))),
                new TiffPage("sample2.tiff", Files.readAllBytes(Path.of("src/test/resources/tiffs/sample2.tiff")))
        );
        ctx.addPageHash("sample1.tiff", "hash-page1");
        ctx.addPageHash("sample2.tiff", "hash-page2");

        // Act
        Path zip = migrationServiceUnderTest.createChainZip("DOC-TEST-001", pages, meta, ctx);

        assertThat(Files.exists(zip)).isTrue();

        // Verify ZIP content
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zip))) {
            ZipEntry entry;
            int tiffCount = 0;
            String manifestContent = null;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().startsWith("page-")) {
                    tiffCount++;
                    assertThat(zis.readAllBytes()).hasSizeGreaterThan(5);
                } else if ("manifest.json".equals(entry.getName())) {
                    manifestContent = new String(zis.readAllBytes());
                }
            }

            assertThat(tiffCount).isEqualTo(2);
            assertThat(manifestContent).isNotNull();
            String expectedManifestContent = """
                    {"docId":"DOC-TEST-001","pageCount":2,"pageHashes":{"sample1.tiff"\
                    :"hash-page1"},"payloadHash":"payload-sha256-abc123","sourceMetadata":\
                    {"docId":"DOC-TEST-001","title":"Test Invoice 2026","clientId":"CHE-123.456.789"}}""";
            JSONAssert.assertEquals(expectedManifestContent, manifestContent, JSONCompareMode.LENIENT);
        }
    }

    /**
     * Create chain zip should fail on null pages.
     */
    @Test
    void createChainZip_shouldFailOnNullPages() {
        assertThatThrownBy(() -> migrationServiceUnderTest.createChainZip("DOC-001", null, meta, ctx))
                .isInstanceOf(NullPointerException.class);
    }

    /**
     * Build xml should fill all relevant fields.
     */
// ────────────────────────────────────────────────
    // buildXml
    // ────────────────────────────────────────────────
    @Test
    void buildXml_shouldFillAllRelevantFields() {
        ctx.setZipHash("zip-xyz789");
        ctx.setPdfHash("pdf-abc456");
        ctx.addPageHash("p1.tif", "h1");
        ctx.addPageHash("p2.tif", "h2");

        ArchivalMetadata xml = migrationServiceUnderTest.buildXml(meta, ctx);

        assertThat(xml.getDocumentId()).isEqualTo("DOC-TEST-001");
        assertThat(xml.getTitle()).isEqualTo("Test Invoice 2026");
        assertThat(xml.getPageCount()).isEqualTo(2);
        assertThat(xml.getPayloadHash()).isEqualTo("payload-sha256-abc123");
        assertThat(xml.getZipHash()).isEqualTo("zip-xyz789");
        assertThat(xml.getPdfHash()).isEqualTo("pdf-abc456");

        assertThat(xml.getProvenance()).isNotNull();
        assertThat(xml.getProvenance().getMigrationTimestamp()).matches("\\d{4}-\\d{2}-\\d{2}T.*Z");
        assertThat(xml.getProvenance().getPageHashes()).hasSize(2);
    }

    /**
     * Build xml should handle missing metadata gracefully.
     */
    @Test
    void buildXml_shouldHandleMissingMetadataGracefully() {
        SourceMetadata nullMeta = new SourceMetadata(); // all fields null

        ArchivalMetadata xml = migrationServiceUnderTest.buildXml(nullMeta, ctx);

        assertThat(xml.getTitle()).isEqualTo("Untitled Document");
        assertThat(xml.getPageCount()).isZero();
        assertThat(xml.getProvenance()).isNotNull();
        assertThat(xml.getCustomFields()).containsEntry("sourceSystem", "legacy-archive-v1");
    }

    /**
     * Merge tiff to pdf should create pdf with correct page count.
     *
     * @throws Exception the exception
     */
// ────────────────────────────────────────────────
    // mergeTiffToPdf
    // ────────────────────────────────────────────────
    @Test
    void mergeTiffToPdf_shouldCreatePdfWithCorrectPageCount() throws Exception {
        List<TiffPage> pages = List.of(
                new TiffPage("sample1.tif", Files.readAllBytes(Path.of("src/test/resources/tiffs/sample1.tiff"))),
                new TiffPage("sample2.tif", Files.readAllBytes(Path.of("src/test/resources/tiffs/sample2.tiff")))
        );
        Path pdfPath = migrationServiceUnderTest.mergeTiffToPdf(pages, "DOC-TEST-PDF");

        assertThat(Files.exists(pdfPath)).isTrue();

        try (PDDocument doc = Loader.loadPDF(pdfPath.toFile())) {
            assertThat(doc.getNumberOfPages()).isEqualTo(2);
            PDPage page1 = doc.getPage(0);
            assertThat(page1.getMediaBox().getWidth()).isEqualTo(480);
            assertThat(page1.getMediaBox().getHeight()).isEqualTo(360);
        }
    }

    /**
     * Merge tiff to pdf should handle empty list.
     *
     * @throws Exception the exception
     */
    @Test
    void mergeTiffToPdf_shouldHandleEmptyList() throws Exception {
        Path pdf = migrationServiceUnderTest.mergeTiffToPdf(List.of(), "DOC-EMPTY");

        try (PDDocument doc = Loader.loadPDF(pdf.toFile())) {
            assertThat(doc.getNumberOfPages()).isZero();
        }
    }

    // Helper to create small test ZIP
    private byte[] createZipWithTiffs(List<String> entries) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < entries.size(); i += 2) {
                zos.putNextEntry(new ZipEntry(entries.get(i)));
                zos.write(entries.get(i + 1).getBytes());
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }
}
