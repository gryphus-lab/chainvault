package ch.gryphus.chainvault.worker;

import ch.gryphus.chainvault.service.MigrationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrepareFilesWorker {
    private static final Logger LOG = LoggerFactory.getLogger(PrepareFilesWorker.class);
    private final MigrationService migrationService;

    @Autowired
    public PrepareFilesWorker(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @JobWorker(type = "prepare-files")
    public void handle(ActivatedJob job) {
        LOG.info("Processing prepare-files job: {}", job.getKey());

        // implementation comes here

        LOG.info("prepare-files job completed: {}", job.getKey());
    }
}
