/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import ch.gryphus.chainvault.model.dto.MigrationEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SseEmitterServiceTest {

    private SseEmitterService sseEmitterServiceUnderTest;

    @BeforeEach
    void setUp() {
        sseEmitterServiceUnderTest = new SseEmitterService();
    }

    @Test
    void testCreateEmitter() {
        // Setup
        // Run the test
        final SseEmitter result = sseEmitterServiceUnderTest.createEmitter("clientId");

        // Verify the results
        assertThat(result).isNotNull();
    }

    @Test
    void testSendEvent() {
        // Setup
        final MigrationEventDto event = new MigrationEventDto();
        event.setId("id");
        event.setMigrationId("migrationId");
        event.setEventType("eventType");
        event.setStepName("stepName");
        event.setMessage("message");

        // Run the test and verify the results
        assertThatNoException().isThrownBy(() -> sseEmitterServiceUnderTest.sendEvent(event));
    }

    @Test
    void testSendEventToClient() {
        // Setup
        final MigrationEventDto event = new MigrationEventDto();
        event.setId("id");
        event.setMigrationId("migrationId");
        event.setEventType("eventType");
        event.setStepName("stepName");
        event.setMessage("message");

        // Run the test and verify the results
        assertThatNoException()
                .isThrownBy(() -> sseEmitterServiceUnderTest.sendEventToClient("clientId", event));
    }
}
