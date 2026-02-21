package ch.gryphus.chainvault.service;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrchestrationService {
    private final CamundaClient client;

    @Autowired
    public OrchestrationService(CamundaClient client) {
        this.client = client;
    }

    public void startProcess() {
        final ProcessInstanceEvent processInstance = client
                .newCreateInstanceCommand()
                .bpmnProcessId("chainvault")
                .latestVersion()
                .send()
                .join();

        log.info("processInstance started with key = {}", processInstance.getProcessInstanceKey());
    }
}
