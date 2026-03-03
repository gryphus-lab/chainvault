/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.domain.SourceMetadata;
import ch.gryphus.chainvault.entity.MigrationAudit;
import ch.gryphus.chainvault.service.AuditEventService;
import ch.gryphus.chainvault.service.MigrationService;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/** The type Transform metadata delegate. */
@Slf4j
@Component("transformMetadata")
@RequiredArgsConstructor
public class TransformMetadataDelegate implements JavaDelegate {

    private final MigrationService migrationService;
    private final AuditEventService auditEventService;

    @Override
    public void execute(DelegateExecution execution) {
        Span span = Span.current();
        String docId = (String) execution.getVariable("docId");
        span.setAttribute("document.id", docId);
        log.info("TransformDocumentDelegate started for docId: {}", docId);

        String piKey = execution.getProcessInstanceId();
        String eventTaskType = "transform-metadata";
        String errorCode = "ASSEMBLY_FAILED";

        auditEventService.updateAuditEventStart(piKey, docId, eventTaskType);

        MigrationContext ctx = (MigrationContext) execution.getTransientVariable("ctx");
        SourceMetadata meta = (SourceMetadata) execution.getTransientVariable("meta");
        try {
            String xml = migrationService.transformMetadataToXml(meta, ctx);
            execution.setTransientVariable("xml", xml);

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
                    "Transform Metadata completed successfully");
        } catch (Exception e) {
            auditEventService.handleException(e, span, piKey, errorCode, eventTaskType);
        }

        log.info("TransformDocumentDelegate completed for docId: {}", docId);
    }
}
