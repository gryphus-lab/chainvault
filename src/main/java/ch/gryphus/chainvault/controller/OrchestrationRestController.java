package ch.gryphus.chainvault.controller;

import ch.gryphus.chainvault.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chainvault")
public class OrchestrationRestController {
    private final OrchestrationService orchestrationService;

    @Autowired
    public OrchestrationRestController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping(value="/process")
    public ResponseEntity<Object> startProcessInstance() {
        String retVal = orchestrationService.startProcess();
        return ResponseEntity.ok().body("Process started with id: %s".formatted(retVal));
    }
}
