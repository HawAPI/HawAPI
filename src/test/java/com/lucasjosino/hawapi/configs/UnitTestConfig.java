package com.lucasjosino.hawapi.configs;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Annotation that can be applied to a unit test class.
 *
 * @see ActiveProfiles
 * @see ExtendWith
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public @interface UnitTestConfig {
}
