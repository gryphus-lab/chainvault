package ch.gryphus.chainvault.service;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SftpUploadWorker {
    private final MigrationService migrationService;

    public SftpUploadWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "upload-sftp")
    public void handle(final ActivatedJob job) {
        log.info("Processing upload-sftp job: {}", job.getKey());

        // implementation comes here

        log.info("upload-sftp job completed: {}", job.getKey());
    }
}
