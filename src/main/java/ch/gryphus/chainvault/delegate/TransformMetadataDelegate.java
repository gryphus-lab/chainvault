package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.domain.SourceMetadata;
import ch.gryphus.chainvault.service.MigrationService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("transformMetadata")
public class TransformMetadataDelegate implements JavaDelegate {

    private final MigrationService migrationService;

    public TransformMetadataDelegate(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");

        log.info("TransformDocumentDelegate started for docId: {}", docId);

        MigrationContext ctx = execution.getVariable("ctx", MigrationContext.class);
        SourceMetadata meta = execution.getVariable("meta", SourceMetadata.class);
        String xml = migrationService.transformMetadataToXml(meta, ctx);
        execution.setVariable("xml", xml);

        log.info("TransformDocumentDelegate completed for docId: {}", docId);
    }
}
