package ch.gryphus.chainvault.controller;

import ch.gryphus.chainvault.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chainvault")
public class OrchestrationRestController {
    private final OrchestrationService orchestrationService;

    @Autowired
    public OrchestrationRestController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping(value="/process")
    public ResponseEntity<Object> startProcessInstance(@RequestBody Map<String, Object> payload) {
        String retVal = orchestrationService.startProcess(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body("Process instance started for payload:%s with id:%s".formatted(payload, retVal));
    }
}
