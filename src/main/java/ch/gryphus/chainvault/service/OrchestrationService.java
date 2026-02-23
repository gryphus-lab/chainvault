package ch.gryphus.chainvault.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrchestrationService {
    private final RuntimeService runtimeService;

    @Autowired
    public OrchestrationService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void startProcess() {
        runtimeService.startProcessInstanceByKey("chainvault");

        log.info("processInstance started with key = {}", runtimeService);
    }
}
