package ch.gryphus.chainvault.controller;

import ch.gryphus.chainvault.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestrationRestController {
    private final OrchestrationService orchestrationService;

    @Autowired
    public OrchestrationRestController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping(value="/process")
    public void startProcessInstance() {
        orchestrationService.startProcess();
    }
}
