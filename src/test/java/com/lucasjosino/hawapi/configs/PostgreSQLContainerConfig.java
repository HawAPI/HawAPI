package com.lucasjosino.hawapi.configs;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * PostgreSQL configuration for {@link Testcontainers}.
 *
 * @see Testcontainers
 * @see ActiveProfiles
 */
@Testcontainers
@ActiveProfiles("test")
abstract public class PostgreSQLContainerConfig {

    private static final String DB_NAME_AND_VERSION = "postgres:15.1";

    private static final String DK_LOCATION = "schema.sql";

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DB_NAME_AND_VERSION)
            .withInitScript(DK_LOCATION)
            .withExposedPorts(5432);

    @DynamicPropertySource
    public static void databaseProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
