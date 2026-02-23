package ch.gryphus.chainvault.controller;

import ch.gryphus.chainvault.service.OrchestrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrchestrationRestController.class)
class OrchestrationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrchestrationService mockOrchestrationService;

    @Test
    void testStartProcessInstance() throws Exception {
        // Given
        when(mockOrchestrationService.startProcess()).thenReturn("test");

        // Run the test and verify the results
        mockMvc.perform(post("/chainvault/process")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Process started with id: test"));
    }
}
