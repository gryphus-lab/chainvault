package ch.gryphus.chainvault.controller;

import ch.gryphus.chainvault.service.OrchestrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The type Orchestration rest controller test.
 */
@WebMvcTest(OrchestrationRestController.class)
class OrchestrationRestControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private OrchestrationService mockOrchestrationService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test start process instance.
     *
     * @throws Exception the exception
     */
    @Test
    void testStartProcessInstance() throws Exception {
        // Setup
        when(mockOrchestrationService.startProcess(any())).thenReturn("test");

        Map<String, Object> variables = Map.of("docId", "123");
        String json = objectMapper.writeValueAsString(variables);

        // Run the test and verify the results
        assertThat(mockMvcTester.post()
                .uri("/chainvault/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyText()
                .contains("docId=123")
                .contains("id:test");
    }
}
