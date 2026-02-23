package ch.gryphus.chainvault.worker;

import ch.gryphus.chainvault.service.MigrationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExtractHashWorker {
    private final MigrationService migrationService;

    public ExtractHashWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "extract-and-hash")
    public void handle(final ActivatedJob job, @Variable(name = "docId") String docId) {
        log.info("Processing extract-and-hash job: {}", job.getKey());

        migrationService.migrateDocument(docId);

        log.info("extract-and-hash job completed: {}", job.getKey());
    }
}
