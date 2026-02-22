package ch.gryphus.chainvault.service;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrepareFilesWorker {
    private static final Logger LOG = LoggerFactory.getLogger(PrepareFilesWorker.class);
    private final MigrationService migrationService;

    public PrepareFilesWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "prepare-files")
    public void handle(final ActivatedJob job) {
        LOG.info("Processing prepare-files job: {}", job.getKey());

        // implementation comes here

        LOG.info("prepare-files job completed: {}", job.getKey());
    }
}
