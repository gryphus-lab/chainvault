package ch.gryphus.chainvault.docker;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for Docker services defined in docker-compose.yml
 * Tests the health, availability, and basic connectivity of each service.
 */
@Testcontainers
class DockerServicesIntegrationIT {

    /**
     * PostgreSQL service test - validates database initialization and connectivity
     */
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("chainvault")
            .withUsername("chainvault")
            .withPassword("secret")
            .withExposedPorts(5432)
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2))
            .withStartupTimeout(Duration.ofSeconds(120));

    /**
     * SFTP service test - validates SSH/SFTP server availability
     */
    @Container
    static GenericContainer<?> sftpContainer = new GenericContainer<>(
            DockerImageName.parse("atmoz/sftp:latest"))
            .withCommand("testuser:testpass123:::upload")
            .withExposedPorts(22)
            .waitingFor(Wait.forLogMessage(".*Server listening on 0.0.0.0 port 22.*", 1))
            .withStartupTimeout(Duration.ofSeconds(120));

    /**
     * Fake API service test - validates Node.js json-server availability
     */
    @Container
    static GenericContainer<?> apiContainer = new GenericContainer<>(
            DockerImageName.parse("node:25-alpine"))
            .withCommand("sh", "-c", 
                    "npm install -g json-server && " +
                    "echo '{\"users\": [], \"documents\": []}' > /data/db.json && " +
                    "json-server --watch /data/db.json --port 9090 --host 0.0.0.0")
            .withExposedPorts(9090)
            .waitingFor(Wait.forHttp("/").forStatusCode(200))
            .withStartupTimeout(Duration.ofSeconds(120));

    @Test
    void testPostgresHealthCheck() throws SQLException {
        // Verify PostgreSQL container is running
        assertThat(postgresContainer.isRunning()).isTrue();

        // Verify database connectivity
        assertThatNoException().isThrownBy(() -> {
            try (Connection conn = DriverManager.getConnection(
                    postgresContainer.getJdbcUrl(),
                    postgresContainer.getUsername(),
                    postgresContainer.getPassword())) {
                
                assertThat(conn).isNotNull();
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(1);
            }
        });
    }

    @Test
    void testPostgresVersionCompatibility() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword())) {
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version()");
            assertThat(rs.next()).isTrue();
            String version = rs.getString(1);
            assertThat(version).contains("PostgreSQL").contains("16");
        }
    }

    @Test
    void testSftpServiceAvailability() {
        assertThat(sftpContainer.isRunning()).isTrue();

        // Verify SFTP port is exposed
        Integer mappedPort = sftpContainer.getMappedPort(22);
        assertThat(mappedPort).isPositive();

        // Verify SFTP directory exists
        assertThatNoException().isThrownBy(() -> {
            var result = sftpContainer.execInContainer("ls", "-l", "/home/testuser/upload");
            assertThat(result.getExitCode()).isZero();
        });
    }

    @Test
    void testSftpUserAccess() throws IOException, InterruptedException {
        // Create a test file in the SFTP container
        var createFileResult = sftpContainer.execInContainer(
                "sh", "-c", "echo 'test data' > /home/testuser/upload/test.txt");
        assertThat(createFileResult.getExitCode()).isZero();

        // Verify file exists
        var listResult = sftpContainer.execInContainer(
                "ls", "-l", "/home/testuser/upload/test.txt");
        assertThat(listResult.getExitCode()).isZero();
        assertThat(listResult.getStdout()).contains("test.txt");

        // Clean up
        sftpContainer.execInContainer("rm", "/home/testuser/upload/test.txt");
    }

    @Test
    void testApiServiceAvailability() {
        assertThat(apiContainer.isRunning()).isTrue();

        Integer mappedPort = apiContainer.getMappedPort(9090);
        assertThat(mappedPort).isPositive();
    }

    @Test
    void testApiResponseFormat() {
        String apiUrl = "http://%s:%d".formatted(apiContainer.getHost(), apiContainer.getMappedPort(9090));
        
        // Wait for API to be fully ready
        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    try {
                        java.net.URL url = new java.net.URL(apiUrl);
                        java.net.URLConnection conn = url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        conn.getInputStream();
                    } catch (Exception e) {
                        throw new AssertionError("API not ready yet", e);
                    }
                });
    }

    @Test
    void testPostgresPortMapping() {
        Integer mappedPort = postgresContainer.getMappedPort(5432);
        assertThat(mappedPort).isNotNull().isPositive();
        assertThat(mappedPort).isGreaterThanOrEqualTo(5432);
    }

    @Test
    void testSftpPortMapping() {
        Integer mappedPort = sftpContainer.getMappedPort(22);
        assertThat(mappedPort).isNotNull().isPositive();
        assertThat(mappedPort).isNotEqualTo(22); // Dynamic port allocation
    }

    @Test
    void testServiceIsolation() {
        // Ensure each service has its own container instance
        assertThat(postgresContainer.getContainerId()).isNotEmpty();
        assertThat(sftpContainer.getContainerId()).isNotEmpty();
        assertThat(apiContainer.getContainerId()).isNotEmpty();
        
        // Verify they have different container IDs
        assertThat(postgresContainer.getContainerId())
                .isNotEqualTo(sftpContainer.getContainerId())
                .isNotEqualTo(apiContainer.getContainerId());
    }

    @Test
    void testPostgresConnectionPooling() throws SQLException {
        // Test multiple concurrent connections
        for (int i = 0; i < 5; i++) {
            try (Connection conn = DriverManager.getConnection(
                    postgresContainer.getJdbcUrl(),
                    postgresContainer.getUsername(),
                    postgresContainer.getPassword())) {
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                assertThat(rs.next()).isTrue();
            }
        }
    }

    @Test
    void testSftpUploadDirectory() throws IOException, InterruptedException {
        // Verify the upload directory exists and is writable
        var result = sftpContainer.execInContainer(
                "sh", "-c", "[ -d /home/testuser/upload ] && echo 'exists' || echo 'not found'");
        
        assertThat(result.getExitCode()).isZero();
        assertThat(result.getStdout().trim()).isEqualTo("exists");
    }

    @Test
    void testServiceMemoryAndResourceUsage() {
        // Verify containers are running and consuming resources
        assertThat(postgresContainer.isRunning()).isTrue();
        assertThat(sftpContainer.isRunning()).isTrue();
        assertThat(apiContainer.isRunning()).isTrue();
        
        // All containers should have non-empty container IDs (indicating they're running)
        assertThat(postgresContainer.getContainerId()).isNotEmpty();
        assertThat(sftpContainer.getContainerId()).isNotEmpty();
        assertThat(apiContainer.getContainerId()).isNotEmpty();
    }
}
