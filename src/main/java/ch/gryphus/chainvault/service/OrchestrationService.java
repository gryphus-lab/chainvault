package ch.gryphus.chainvault.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
public class OrchestrationService {
    private final RuntimeService runtimeService;

    @Autowired
    public OrchestrationService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Transactional
    public String startProcess(Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("chainvault", variables);
        return processInstance.getProcessInstanceId();
    }
}
