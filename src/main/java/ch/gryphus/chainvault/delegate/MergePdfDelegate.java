package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
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
@Component("mergePdf")
public class MergePdfDelegate implements JavaDelegate {
    private final MigrationService migrationService;

    public MergePdfDelegate(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");
        log.info("MergePdfDelegate started for docId:{}", docId);

        var pages = execution.getVariable("pages", List.class);
        MigrationContext ctx = execution.getVariable("ctx", MigrationContext.class);

        Path pdfPath = migrationService.mergeTiffToPdf(pages, docId);
        ctx.setPdfHash(HashUtils.sha256(pdfPath));

        execution.setVariable("ctx", ctx);
        execution.setVariable("pdfPath", pdfPath);

        log.info("MergePdfDelegate completed for docId:{}", docId);
    }
}
