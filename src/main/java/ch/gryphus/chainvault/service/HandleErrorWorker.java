package ch.gryphus.chainvault.service;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HandleErrorWorker {
    private final MigrationService migrationService;

    public HandleErrorWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "handle-error")
    public void handle(final ActivatedJob job) {
        log.info("Processing handle-error job: {}", job.getKey());

        // implementation comes here

        log.info("handle-error job completed: {}", job.getKey());
    }
}
