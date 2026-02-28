/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class MigrationServiceRestClientTest extends BaseIT {

    /**
     * The constant jsonServer.
     */
    // Fake REST API (json-server with db.json)
    @Container
    static GenericContainer<?> jsonServer =
            new GenericContainer<>(DockerImageName.parse("node:25-alpine"))
                    .withPrivilegedMode(true)
                    .withCommand(
                            "sh",
                            "-c",
                            "npm install -g json-server && json-server --watch /data/db.json"
                                    + " --static /data/static --port 9090 --host 0.0.0.0")
                    .withClasspathResourceMapping("db.json", "/data/db.json", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("static", "/data/static", BindMode.READ_ONLY)
                    .withExposedPorts(9090)
                    .waitingFor(Wait.forHttp("/").forStatusCode(200));

    /**
     * The constant sftpContainer.
     */
    @Container
    static GenericContainer<?> sftpContainer =
            new GenericContainer<>(DockerImageName.parse("atmoz/sftp:latest"))
                    .withCommand("testuser:testpass123:::upload")
                    .withExposedPorts(22)
                    .waitingFor(Wait.forLogMessage(".*Server listening on 0.0.0.0 port 22.*", 1));

    @Autowired private MigrationService migrationService;

    /**
     * Configure properties.
     *
     * @param registry the registry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Fake source API
        String apiUrl =
                "http://%s:%d".formatted(jsonServer.getHost(), jsonServer.getMappedPort(9090));
        registry.add("source.api.base-url", () -> apiUrl);
        registry.add("source.api.token", () -> "dummy-token");

        // Postgres
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // SFTP
        registry.add("target.sftp.host", sftpContainer::getHost);
        registry.add("target.sftp.port", () -> String.valueOf(sftpContainer.getMappedPort(22)));
        registry.add("target.sftp.username", () -> "testuser");
        registry.add("target.sftp.password", () -> "testpass123");
        registry.add("target.sftp.remote-directory", () -> "/upload");
        registry.add("target.sftp.allow-unknown-keys", () -> "true");
    }

    /**
     * Migrate document should upload to real sftp.
     *
     */
    @Test
    void testExtractAndHash_whenDocumentExists() throws NoSuchAlgorithmException {
        String docId = "DOC-ARCH-20250115-001"; // exists in your verbose db.json
        Map<String, Object> result = migrationService.extractAndHash(docId);

        assertThat(result).isNotNull();
    }

    /**
     * Migrate document with non existing doc should fail gracefully.
     */
    @Test
    void testExtractAndHash_whenDocumentDoesNotExist() {
        String invalidId = "DOC-NOT-EXISTS-999";
        assertThatException().isThrownBy(() -> migrationService.extractAndHash(invalidId));
    }
}
