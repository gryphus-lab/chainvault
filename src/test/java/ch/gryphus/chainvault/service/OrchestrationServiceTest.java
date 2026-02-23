package ch.gryphus.chainvault.service;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrchestrationServiceTest {

    @Mock
    private RuntimeService mockRuntimeService;

    private OrchestrationService orchestrationServiceUnderTest;

    @Mock
    private ProcessInstance mockProcessInstance;

    @BeforeEach
    void setUp() {
        orchestrationServiceUnderTest = new OrchestrationService(mockRuntimeService);
        when(mockProcessInstance.getProcessInstanceId()).thenReturn("test");
    }

    @Test
    void testStartProcess() {
        // Setup
        final Map<String, Object> variables = Map.ofEntries(Map.entry("value", "value"));
        when(mockRuntimeService.startProcessInstanceByKey(anyString(), anyMap())).thenReturn(mockProcessInstance);

        // Run the test
        final String result = orchestrationServiceUnderTest.startProcess(variables);

        // Verify the results
        assertThat(result).isEqualTo("test");
    }
}
