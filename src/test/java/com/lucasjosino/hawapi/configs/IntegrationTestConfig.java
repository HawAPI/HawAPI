package com.lucasjosino.hawapi.configs;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Annotation that can be applied to an integration test class.
 *
 * @see AutoConfigureMockMvc
 * @see ActiveProfiles
 * @see SpringBootTest
 */
@Inherited
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface IntegrationTestConfig {
}
