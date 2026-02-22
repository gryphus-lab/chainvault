package ch.gryphus.chainvault.service;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SignDocumentWorker {
    private final MigrationService migrationService;

    public SignDocumentWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "sign-document")
    public void handle(final ActivatedJob job) {
        log.info("Processing sign-document job: {}", job.getKey());

        // implementation comes here

        log.info("sign-document job completed: {}", job.getKey());
    }
}
