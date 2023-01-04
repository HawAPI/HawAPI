package com.lucasjosino.hawapi.configs;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
@UnitTestConfig
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface RepositoryUnitTestConfig {
}
