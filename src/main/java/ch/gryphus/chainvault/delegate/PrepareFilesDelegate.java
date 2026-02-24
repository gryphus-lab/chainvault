package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.domain.SourceMetadata;
import ch.gryphus.chainvault.service.MigrationService;
import ch.gryphus.chainvault.utils.HashUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component("prepareFiles")
public class PrepareFilesDelegate implements JavaDelegate {

    private final MigrationService migrationService;

    public PrepareFilesDelegate(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");
        log.info("PrepareFilesDelegate started for docId:{}", docId);

        var pages = execution.getVariable("pages", List.class);
        SourceMetadata meta =  execution.getVariable("meta", SourceMetadata.class);
        MigrationContext ctx = execution.getVariable("ctx", MigrationContext.class);
        Path zipPath = migrationService.createChainZip(docId, pages, meta, ctx);
        ctx.setZipHash(HashUtils.sha256(zipPath));

        execution.setVariable("ctx", ctx);
        execution.setVariable("zipPath", zipPath);

        log.info("PrepareFilesDelegate completed for docId:{}", docId);
    }
}
