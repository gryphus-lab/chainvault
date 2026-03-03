/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.domain.TiffPage;
import ch.gryphus.chainvault.entity.MigrationAudit;
import ch.gryphus.chainvault.service.AuditEventService;
import ch.gryphus.chainvault.service.MigrationService;
import ch.gryphus.chainvault.utils.HashUtils;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * The type Merge pdf delegate.
 */
@Slf4j
@Component("mergePdf")
@RequiredArgsConstructor
public class MergePdfDelegate implements JavaDelegate {
    private final MigrationService migrationService;
    private final AuditEventService auditEventService;

    @Override
    public void execute(DelegateExecution execution) {
        Span span = Span.current();
        String docId = (String) execution.getVariable("docId");
        span.setAttribute("document.id", docId);
        log.info("MergePdfDelegate started for docId:{}", docId);

        String piKey = execution.getProcessInstanceId();
        String eventTaskType = "merge-pdf";
        String errorCode = "ASSEMBLY_FAILED";

        auditEventService.updateAuditEventStart(piKey, docId, eventTaskType);

        List<TiffPage> pages = (List<TiffPage>) execution.getTransientVariable("pages");
        MigrationContext ctx = (MigrationContext) execution.getTransientVariable("ctx");

        Path pdfPath;
        try {
            pdfPath = migrationService.mergeTiffToPdf(pages, docId);
            ctx.setPdfHash(HashUtils.sha256(pdfPath));

            execution.setTransientVariable("ctx", ctx);
            execution.setTransientVariable("pdfPath", pdfPath);

            // Record success event
            span.addEvent(
                    "%s.success".formatted(eventTaskType),
                    Attributes.of(AttributeKey.stringKey("document.id"), docId));

            auditEventService.updateAuditEventEnd(
                    piKey,
                    MigrationAudit.MigrationStatus.SUCCESS,
                    null,
                    null,
                    eventTaskType,
                    "Merge PDF completed successfully");
        } catch (IOException | NoSuchAlgorithmException e) {
            auditEventService.handleException(e, span, piKey, errorCode, eventTaskType);
        }

        log.info("MergePdfDelegate completed for docId:{}", docId);
    }
}
