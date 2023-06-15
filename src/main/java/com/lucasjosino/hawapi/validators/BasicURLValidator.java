package com.lucasjosino.hawapi.validators;

import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Basic url validator.
 *
 * @author Lucas Josino
 * @see BasicURL
 * @since 1.0.0
 */
public class BasicURLValidator implements ConstraintValidator<BasicURL, String> {

    /**
     * Define if element must be secure (using https).
     */
    private boolean isSecure;

    /**
     * Define if element must be an image (jpg, jpeg, png).
     */
    private boolean isImage;

    @Override
    public void initialize(BasicURL constraintAnnotation) {
        isSecure = constraintAnnotation.secure();
        isImage = constraintAnnotation.image();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isSecure && !value.startsWith("https://")) return false;

        if (isImage) return Pattern.compile(".(jpg|png|jpeg)$").matcher(value).find();

        try {
            new URL(value).toURI();
            return Pattern.compile("^(http(s)://)").matcher(value).find();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}