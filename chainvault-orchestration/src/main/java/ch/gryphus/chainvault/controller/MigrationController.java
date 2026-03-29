/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.controller;

import ch.gryphus.chainvault.model.entity.MigrationAudit;
import ch.gryphus.chainvault.model.entity.MigrationDetail;
import ch.gryphus.chainvault.model.entity.MigrationStats;
import ch.gryphus.chainvault.workflow.service.AuditEventService;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/migrations") // ← This must be present
public class MigrationController {

    private final AuditEventService auditEventService;

    public MigrationController(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    @GetMapping
    public List<MigrationAudit> getMigrations(@RequestParam(defaultValue = "100") int limit) {
        return auditEventService.getMigrations(limit);
    }

    @GetMapping("/stats")
    public MigrationStats getStats() {
        return auditEventService.getStats();
    }

    @GetMapping("/{id}/detail")
    public MigrationDetail getDetail(@PathVariable String id) {
        return auditEventService.getDetail(id);
    }
}
