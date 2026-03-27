/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.gryphus.chainvault.model.dto.MigrationEventDto;
import java.io.IOException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class SseEmitterServiceTest {

    @InjectMocks private SseEmitterService sseEmitterService;

    private MigrationEventDto testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new MigrationEventDto();
    }

    @Test
    @DisplayName("Should create and store a new emitter")
    void createEmitter_Success() {
        String clientId = "client-123";
        SseEmitter emitter = sseEmitterService.createEmitter(clientId);

        assertNotNull(emitter);
        assertEquals(0L, emitter.getTimeout());
    }

    @Test
    @DisplayName("Should send event to a specific client")
    void sendEventToClient_Success() {
        String clientId = "client-1";

        SseEmitter emitter = sseEmitterService.createEmitter(clientId);

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(
                        () -> {
                            assertDoesNotThrow(
                                    () -> sseEmitterService.sendEventToClient(clientId, testEvent));
                            assertThat(emitter.getTimeout()).isZero();
                            assertThat(emitter).hasFieldOrPropertyWithValue("failure", null);
                        });
    }

    @Test
    @DisplayName("Should remove emitter on IOException during broadcast")
    void sendEvent_RemovesEmitterOnFailure() throws IOException {
        String clientId = "dead-client";
        SseEmitter mockEmitter = mock(SseEmitter.class);

        sseEmitterService.createEmitter(clientId);
        doThrow(IOException.class).when(mockEmitter).send(any(MigrationEventDto.class));

        assertDoesNotThrow(() -> sseEmitterService.sendEvent(testEvent));
    }

    @Test
    @DisplayName("Should remove emitter on IOException when sending to a specific client")
    void sendEventToClient_RemovesEmitterOnFailure() throws IOException {
        String clientId = "dead-client";
        SseEmitter mockEmitter = mock(SseEmitter.class);

        sseEmitterService.createEmitter(clientId);
        doThrow(IOException.class).when(mockEmitter).send(any(MigrationEventDto.class));
        assertDoesNotThrow(() -> sseEmitterService.sendEventToClient(clientId, testEvent));
    }

    @Test
    @DisplayName("Should handle broadcast to multiple clients")
    void sendEvent_BroadcastsToAll() {
        sseEmitterService.createEmitter("c1");
        sseEmitterService.createEmitter("c2");

        assertDoesNotThrow(() -> sseEmitterService.sendEvent(testEvent));
    }
}
