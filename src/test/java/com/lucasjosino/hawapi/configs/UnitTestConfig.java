package com.lucasjosino.hawapi.configs;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Annotation that can be applied to a unit test class.
 *
 * @see ActiveProfiles
 * @see ExtendWith
 */
@Inherited
@ActiveProfiles("test")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
public @interface UnitTestConfig {
}
