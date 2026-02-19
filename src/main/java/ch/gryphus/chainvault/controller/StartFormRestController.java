package ch.gryphus.chainvault.controller;

import io.camunda.client.CamundaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class StartFormRestController {
    private final CamundaClient client;

    @Autowired
    public StartFormRestController(CamundaClient client) {
        this.client = client;
    }

    @PostMapping("/start")
    public void startProcessInstance(@RequestBody Map<String, Object> variables) {
        log.info("Starting process `chainvault` with variables: {}", variables);
        client
                .newCreateInstanceCommand()
                .bpmnProcessId("chainvault")
                .latestVersion()
                .variables(variables)
                .send()
                .join();
    }
}