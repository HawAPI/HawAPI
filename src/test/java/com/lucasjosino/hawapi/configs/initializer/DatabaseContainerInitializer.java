package com.lucasjosino.hawapi.configs.initializer;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

/**
 * Database configuration for tests using {@link Testcontainers}.
 */
@ActiveProfiles("test")
abstract public class DatabaseContainerInitializer {

    private static final String DB_NAME_AND_VERSION = "postgres:15.1";

    private static final String DB_ENTRY_POINT = "/docker-entrypoint-initdb.d/";

    private static final String SCHEMA_LOCATION = "schema.sql";

    @Container
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DB_NAME_AND_VERSION)
            .withCopyFileToContainer(MountableFile.forClasspathResource(SCHEMA_LOCATION), DB_ENTRY_POINT)
            .withExposedPorts(5432);

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void databaseProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
