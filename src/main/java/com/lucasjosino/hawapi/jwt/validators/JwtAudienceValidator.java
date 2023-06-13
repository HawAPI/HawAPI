package com.lucasjosino.hawapi.jwt.validators;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Validates the "aud" claim in a {@link Jwt}, that is matches a configured value
 *
 * @author Lucas Josino
 * @see Jwt
 * @see JwtIssuerValidator
 * @see OAuth2TokenValidator
 * @since 1.0.0
 */
public final class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final JwtClaimValidator<Object> validator;

    /**
     * Constructs a {@link JwtAudienceValidator} using the provided parameters
     *
     * @param aud - The aud that each {@link Jwt} should have.
     */
    public JwtAudienceValidator(String aud) {
        Assert.notNull(aud, "audience cannot be null");

        Predicate<Object> testClaimValue = (claimValue) ->
                claimValue != null && ((ArrayList<?>) claimValue).contains(aud);

        this.validator = new JwtClaimValidator<>("aud", testClaimValue);
    }

    public OAuth2TokenValidatorResult validate(Jwt token) {
        Assert.notNull(token, "token cannot be null");
        return this.validator.validate(token);
    }
}
