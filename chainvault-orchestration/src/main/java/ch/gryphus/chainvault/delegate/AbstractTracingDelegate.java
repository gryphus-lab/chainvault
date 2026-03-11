/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.config.Constants;
import ch.gryphus.chainvault.entity.MigrationAudit;
import ch.gryphus.chainvault.service.AuditEventService;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.*;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * The type Abstract tracing delegate.
 */
public abstract class AbstractTracingDelegate implements JavaDelegate {

    protected abstract AuditEventService getAuditEventService();

    protected abstract String getTaskType();

    protected abstract String getErrorCode();

    @Override
    public void execute(DelegateExecution execution) {
        // 1. Reconstruct Context from stored TraceParent string
        String traceParent = (String) execution.getVariable("traceParent");
        Context parentContext = OTelUtils.extractContextFromTraceParent(traceParent);

        // 2. Start a child span
        Span span =
                GlobalOpenTelemetry.getTracer("chainvault-tracer")
                        .spanBuilder(getTaskType())
                        .setParent(parentContext)
                        .setSpanKind(SpanKind.INTERNAL)
                        .startSpan();

        try (Scope scope = span.makeCurrent()) {
            executeManagedStep(execution, span);
        } catch (Exception e) {
            span.recordException(e);
            getAuditEventService()
                    .handleException(
                            e,
                            span,
                            execution.getProcessInstanceId(),
                            getErrorCode(),
                            getTaskType());
        } finally {
            span.end();
        }
    }

    private void executeManagedStep(DelegateExecution execution, Span span)
            throws IOException, NoSuchAlgorithmException {
        String docId = (String) execution.getVariable(Constants.BPMN_PROC_VAR_DOC_ID);
        span.setAttribute(Constants.SPAN_ATTR_DOCUMENT_ID, docId);

        getAuditEventService()
                .updateAuditEventStart(
                        execution.getProcessInstanceId(), docId, getTaskType(), span);

        // Execute unique business logic
        doExecute(execution, span, docId);

        span.addEvent(
                getTaskType() + ".success",
                Attributes.of(AttributeKey.stringKey(Constants.SPAN_ATTR_DOCUMENT_ID), docId));
        getAuditEventService()
                .updateAuditEventEnd(
                        execution.getProcessInstanceId(),
                        MigrationAudit.MigrationStatus.SUCCESS,
                        null,
                        null,
                        getTaskType(),
                        "%s completed".formatted(getTaskType()),
                        execution.getTransientVariables());
    }

    protected abstract void doExecute(DelegateExecution execution, Span span, String docId)
            throws IOException, NoSuchAlgorithmException;
}
