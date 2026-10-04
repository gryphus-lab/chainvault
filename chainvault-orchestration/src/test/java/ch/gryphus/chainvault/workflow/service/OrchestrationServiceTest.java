/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.gryphus.chainvault.config.Constants;
import ch.gryphus.chainvault.model.entity.MigrationAudit;
import ch.gryphus.chainvault.repository.MigrationAuditRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import java.util.Map;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * The type Orchestration service test.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class OrchestrationServiceTest {
    @Mock private RuntimeService mockRuntimeService;
    @Mock private MigrationAuditRepository auditRepository;
    @Mock private ProcessInstance mockProcessInstance;
    @Mock private Tracer mockTracer;
    @Mock private SpanBuilder mockSpanBuilder;
    @Mock private Span mockSpan;
    @Mock private Context mockContext;
    @Mock private SpanContext mockSpanContext;

    private OrchestrationService orchestrationServiceUnderTest;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        orchestrationServiceUnderTest =
                new OrchestrationService(mockRuntimeService, auditRepository, mockTracer);

        when(mockTracer.spanBuilder(any())).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan);
        when(Context.current().with(mockSpan)).thenReturn(mockContext);
        when(mockSpan.getSpanContext()).thenReturn(mockSpanContext);
        when(mockSpanContext.getTraceId()).thenReturn("test-traceId");
        when(mockSpanContext.getSpanId()).thenReturn("test-spanId");
        when(mockProcessInstance.getProcessInstanceId()).thenReturn("test");
    }

    /**
     * Test start process.
     */
    @Test
    void testStartProcess() {
        // Setup
        Map<String, Object> variables =
                Map.ofEntries(Map.entry(Constants.BPMN_PROC_VAR_DOC_ID, "123"));
        when(mockRuntimeService.startProcessInstanceByKey(anyString(), anyMap()))
                .thenReturn(mockProcessInstance);
        when(mockProcessInstance.getProcessDefinitionKey()).thenReturn("test-definition");

        // Run the test
        String result = orchestrationServiceUnderTest.startProcess(variables);

        // Verify the results
        assertThat(result).isEqualTo("test");

        ArgumentCaptor<MigrationAudit> auditCaptor = ArgumentCaptor.forClass(MigrationAudit.class);
        verify(auditRepository).save(auditCaptor.capture());

        MigrationAudit savedAudit = auditCaptor.getValue();
        assertThat(savedAudit.getProcessInstanceKey()).isEqualTo("test");
        assertThat(savedAudit.getProcessDefinitionKey()).isEqualTo("test-definition");
        assertThat(savedAudit.getBpmnProcessId()).isEqualTo(Constants.BPMN_PROCESS_DEFINITION_KEY);
        assertThat(savedAudit.getDocumentId()).isEqualTo("123");
        assertThat(savedAudit.getStatus()).isEqualTo(MigrationAudit.MigrationStatus.PENDING);
        assertThat(savedAudit.getTraceId()).isNotBlank();
    }

    @Test
    void testStartProcessWithoutDocumentId() {
        Map<String, Object> variables = Map.of();
        when(mockRuntimeService.startProcessInstanceByKey(anyString(), anyMap()))
                .thenReturn(mockProcessInstance);
        when(mockProcessInstance.getProcessDefinitionKey()).thenReturn("fallback-definition");

        String result = orchestrationServiceUnderTest.startProcess(variables);

        assertThat(result).isEqualTo("test");

        ArgumentCaptor<MigrationAudit> auditCaptor = ArgumentCaptor.forClass(MigrationAudit.class);
        verify(auditRepository).save(auditCaptor.capture());
        assertThat(auditCaptor.getValue().getDocumentId()).isNull();
    }
}
