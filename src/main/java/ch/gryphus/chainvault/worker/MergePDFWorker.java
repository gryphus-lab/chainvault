package ch.gryphus.chainvault.worker;

import ch.gryphus.chainvault.service.MigrationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MergePDFWorker {
    private final MigrationService migrationService;

    public MergePDFWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "merge-to-pdf")
    public void handle(final ActivatedJob job) {
        log.info("Processing merge-to-pdf job: {}", job.getKey());

        // implementation comes here

        log.info("merge-to-pdf job completed: {}", job.getKey());
    }
}
