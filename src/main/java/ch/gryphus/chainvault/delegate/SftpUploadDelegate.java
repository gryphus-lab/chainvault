package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.service.MigrationService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component("uploadSftp")
public class SftpUploadDelegate implements JavaDelegate {
    private final MigrationService migrationService;

    public SftpUploadDelegate(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");

        log.info("SftpUploadDelegate started for docId: {}", docId);

        MigrationContext ctx = execution.getVariable("ctx", MigrationContext.class);
        String xml = (String) execution.getVariable("xml");
        Path zipPath = execution.getVariable("zipPath", Path.class);
        Path pdfPath = execution.getVariable("pdfPath", Path.class);

        migrationService.uploadToSftp(ctx, docId, xml, zipPath, pdfPath);

        log.info("SftpUploadDelegate completed for docId: {}", docId);
    }
}
