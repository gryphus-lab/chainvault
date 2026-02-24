package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.service.MigrationService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("extractAndHash")
public class ExtractAndHashDelegate implements JavaDelegate {

    private final MigrationService migrationService;

    public ExtractAndHashDelegate(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");

        log.info("SignDocumentDelegate started for docId: {}", docId);

        Map<String, Object> map = migrationService.extractAndHash(docId);

        execution.setVariable("ctx", map.get("ctx"));
        execution.setVariable("meta", map.get("meta"));
        execution.setVariable("payload", map.get("payload"));

        log.info("SignDocumentDelegate completed for docId: {}", docId);

    }
}
