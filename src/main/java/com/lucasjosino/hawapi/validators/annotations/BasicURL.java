package com.lucasjosino.hawapi.validators.annotations;

import com.lucasjosino.hawapi.validators.BasicURLValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The annotated element must be a valid basic url.
 *
 * @author Lucas Josino
 * @see BasicURLValidator
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BasicURLValidator.class)
public @interface BasicURL {

    /**
     * Define if element must be secure (using https).
     */
    boolean secure() default true;

    /**
     * Define if element must be an image (jpg, jpeg, png).
     */
    boolean image() default false;

    /**
     * Define the error message.
     */
    String message() default "Provide a valid URL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
