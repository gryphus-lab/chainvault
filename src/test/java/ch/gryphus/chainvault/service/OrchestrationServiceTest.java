package ch.gryphus.chainvault.service;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrchestrationServiceTest {

    @Mock
    private RuntimeService mockRuntimeService;

    private OrchestrationService orchestrationServiceUnderTest;

    @Mock
    private ProcessInstance processInstance;

    @BeforeEach
    void setUp() {
        orchestrationServiceUnderTest = new OrchestrationService(mockRuntimeService);
        when(processInstance.getProcessInstanceId()).thenReturn("test");
    }

    @Test
    void testStartProcess() {
        // Setup
        when(mockRuntimeService.startProcessInstanceByKey("chainvault")).thenReturn(processInstance);

        // Run the test
        final String result = orchestrationServiceUnderTest.startProcess();

        // Verify the results
        assertThat(result).isEqualTo("test");
    }
}
