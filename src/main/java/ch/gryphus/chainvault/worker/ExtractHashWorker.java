package ch.gryphus.chainvault.worker;

import ch.gryphus.chainvault.service.MigrationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ExtractHashWorker {
    private final MigrationService migrationService;

    public ExtractHashWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "extract-and-hash")
    public void handle(final ActivatedJob job) {
        log.info("Processing extract-and-hash job: {}", job.getKey());
        Map<String, Object> variables = job.getVariablesAsMap();

        String docId = (String) variables.get("docId");
        migrationService.migrateDocument(docId);

        log.info("extract-and-hash job completed: {}", job.getKey());
    }
}
