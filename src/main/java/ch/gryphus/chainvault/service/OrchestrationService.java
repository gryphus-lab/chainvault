package ch.gryphus.chainvault.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
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

    public String startProcess() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("chainvault");
        return processInstance.getProcessInstanceId();
    }
}
