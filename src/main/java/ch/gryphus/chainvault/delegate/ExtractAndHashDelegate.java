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

        log.info("ExtractAndHashDelegate started for docId: {}", docId);

        Map<String, Object> map = migrationService.extractAndHash(docId);

        execution.setTransientVariable("ctx", map.get("ctx"));
        execution.setTransientVariable("meta", map.get("meta"));
        execution.setTransientVariable("payload", map.get("payload"));

        log.info("ExtractAndHashDelegate completed for docId: {}", docId);

    }
}
