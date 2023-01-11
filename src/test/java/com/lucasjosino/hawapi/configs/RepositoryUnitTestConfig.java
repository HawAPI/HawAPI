package com.lucasjosino.hawapi.configs;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * Annotation that can be applied to a test class to configure a test repository.
 *
 * @see DataJpaTest
 * @see UnitTestConfig
 * @see AutoConfigureTestDatabase
 */
@Inherited
@DataJpaTest
@Transactional
@UnitTestConfig
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface RepositoryUnitTestConfig {
}
