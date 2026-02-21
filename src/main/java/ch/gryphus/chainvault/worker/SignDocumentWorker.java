package ch.gryphus.chainvault.worker;

import ch.gryphus.chainvault.service.MigrationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignDocumentWorker {
    private final MigrationService migrationService;

    @JobWorker(type = "sign-document")
    public void handle(ActivatedJob job) {
        log.info("Processing sign-document job: {}", job.getKey());

        // implementation comes here

        log.info("sign-document job completed: {}", job.getKey());
    }
}
