package com.lucasjosino.hawapi.configs;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Annotation that can be applied to a unit test class.
 */
@Inherited
@ActiveProfiles("test")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public @interface UnitTestConfig {
}
