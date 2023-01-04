package com.lucasjosino.hawapi.configs;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Annotation that can be applied to a test class to configure a test repository.
 *
 * @see DataJpaTest
 * @see UnitTestConfig
 * @see AutoConfigureTestDatabase
 */
@DataJpaTest
@UnitTestConfig
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface RepositoryUnitTestConfig {
}
