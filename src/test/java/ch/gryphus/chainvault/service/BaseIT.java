package ch.gryphus.chainvault.service;

import org.testcontainers.containers.BindMode;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseIT {
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("chainvault")
            .withUsername("chainvault")
            .withPassword("secret")
            .withClasspathResourceMapping("db/init-scripts", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY);

    static {
        postgres.start();
        // Manual hook that runs lates
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (postgres.isRunning()) {
                postgres.stop();
            }
        }));
    }
}