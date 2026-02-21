package ch.gryphus.chainvault.worker;

import ch.gryphus.chainvault.service.MigrationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtractHashWorker {
    private final MigrationService migrationService;

    @JobWorker(type = "extract-and-hash")
    public void handle(ActivatedJob job) {
        log.info("Processing extract-and-hash job: {}", job.getKey());

        // implementation comes here

        log.info("extract-and-hash job completed: {}", job.getKey());
    }
}
