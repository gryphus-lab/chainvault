package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.domain.SourceMetadata;
import ch.gryphus.chainvault.domain.TiffPage;
import ch.gryphus.chainvault.service.MigrationService;
import ch.gryphus.chainvault.utils.HashUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Component("prepareFiles")
public class PrepareFilesDelegate implements JavaDelegate {

    private final MigrationService migrationService;

    public PrepareFilesDelegate(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");
        log.info("PrepareFilesDelegate started for docId:{}", docId);

        var pages = (List<TiffPage>) execution.getTransientVariable("pages");
        SourceMetadata meta = (SourceMetadata) execution.getTransientVariable("meta");
        MigrationContext ctx = (MigrationContext) execution.getTransientVariable("ctx");
        Path zipPath;
        try {
            zipPath = migrationService.createChainZip(docId, pages, meta, ctx);
            ctx.setZipHash(HashUtils.sha256(zipPath));
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("error preparing files", e);
        }

        execution.setTransientVariable("ctx", ctx);
        execution.setTransientVariable("zipPath", zipPath);

        log.info("PrepareFilesDelegate completed for docId:{}", docId);
    }
}
