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
public class TransformMetadataWorker {
    private final MigrationService migrationService;

    @JobWorker(type = "transform-metadata")
    public void handle(ActivatedJob job) {
        log.info("Processing transform-metadata job: {}", job.getKey());

        // implementation comes here

        log.info("transform-metadata job completed: {}", job.getKey());
    }
}
