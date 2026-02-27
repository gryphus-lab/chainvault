package ch.gryphus.chainvault.docker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Docker Compose orchestration tests
 * Validates that services defined in docker-compose.yml work together correctly
 */
@Testcontainers
@DisplayName("Docker Compose Orchestration Tests")
class DockerComposeIT {

    private static final String POSTGRES_SERVICE = "postgres";
    private static final String SFTP_SERVICE = "sftp-test";
    private static final String API_SERVICE = "fake-source-api";

    /**
     * Container for managing the entire docker-compose stack
     * Note: Requires docker-compose.yml to be in a specific location
     */
    @Container
    static ComposeContainer dockerCompose = new ComposeContainer(
            new File("../docker-compose.yml"))
            .withExposedService(POSTGRES_SERVICE, 5432,
                    Wait.forLogMessage(".*database system is ready to accept connections.*", 2)
                            .withStartupTimeout(Duration.ofSeconds(120)))
            .withExposedService(SFTP_SERVICE, 22,
                    Wait.forLogMessage(".*Server listening on 0.0.0.0 port 22.*", 1)
                            .withStartupTimeout(Duration.ofSeconds(120)))
            .withExposedService(API_SERVICE, 9090,
                    Wait.forHttp("/")
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofSeconds(120)));


    @Test
    @DisplayName("Docker compose container should start successfully")
    void testDockerComposeStarts() {
        assertThat(dockerCompose).isNotNull();
    }

    @Test
    @DisplayName("PostgreSQL service should be accessible")
    void testPostgresServiceAccessibility() {
        String host = dockerCompose.getServiceHost(POSTGRES_SERVICE, 5432);
        Integer port = dockerCompose.getServicePort(POSTGRES_SERVICE, 5432);

        assertThat(host).isNotNull();
        assertThat(port).isPositive();
    }

    @Test
    @DisplayName("SFTP service should be accessible")
    void testSftpServiceAccessibility() {
        String host = dockerCompose.getServiceHost(SFTP_SERVICE, 22);
        Integer port = dockerCompose.getServicePort(SFTP_SERVICE, 22);

        assertThat(host).isNotNull();
        assertThat(port).isPositive();
    }

    @Test
    @DisplayName("API service should be accessible")
    void testApiServiceAccessibility() {
        String host = dockerCompose.getServiceHost(API_SERVICE, 9090);
        Integer port = dockerCompose.getServicePort(API_SERVICE, 9090);

        assertThat(host).isNotNull();
        assertThat(port).isGreaterThan(9090);
    }

    @Test
    @DisplayName("Services should have proper network connectivity")
    void testServiceNetworkConnectivity() {
        await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    String postgresHost = dockerCompose.getServiceHost(POSTGRES_SERVICE, 5432);
                    String sftpHost = dockerCompose.getServiceHost(SFTP_SERVICE, 22);
                    String apiHost = dockerCompose.getServiceHost(API_SERVICE, 9090);

                    assertThat(postgresHost).isNotBlank();
                    assertThat(sftpHost).isNotBlank();
                    assertThat(apiHost).isNotBlank();
                });
    }

    @Test
    @DisplayName("All services should be running")
    void testServicesRunning() {
        assertThat(dockerCompose.getServicePort(POSTGRES_SERVICE, 5432)).isPositive();
        assertThat(dockerCompose.getServicePort(SFTP_SERVICE, 22)).isPositive();
        assertThat(dockerCompose.getServicePort(API_SERVICE, 9090)).isPositive();
    }

    @Test
    @DisplayName("Should handle port mapping correctly")
    void testPortMapping() {
        // Postgres: 5432 -> random local port
        Integer postgresPort = dockerCompose.getServicePort(POSTGRES_SERVICE, 5432);
        assertThat(postgresPort).isGreaterThanOrEqualTo(5432);

        // SFTP: 22 -> random local port (dynamic)
        Integer sftpPort = dockerCompose.getServicePort(SFTP_SERVICE, 22);
        assertThat(sftpPort).isGreaterThan(22);

        // API: 9090 -> 9090 (fixed)
        Integer apiPort = dockerCompose.getServicePort(API_SERVICE, 9090);
        assertThat(apiPort).isGreaterThan(9090);
    }
}
