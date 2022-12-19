package com.lucasjosino.hawapi.jwt.validators;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.function.Predicate;

public final class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final JwtClaimValidator<Object> validator;

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
