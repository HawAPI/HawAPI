package com.lucasjosino.hawapi.jwt.validators;

import com.lucasjosino.hawapi.repositories.auth.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtUserValidator implements OAuth2TokenValidator<Jwt> {

    private final AuthRepository authRepository;

    @Autowired
    public JwtUserValidator(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String username = token.getClaim("username");
        String email = token.getClaim("email");

        if (username == null || email == null) {
            return OAuth2TokenValidatorResult.failure(
                    this.createOAuth2Error("Username or Email not found on token")
            );
        }

        if (authRepository.existsByUsernameAndEmail(username, email)) {
            return OAuth2TokenValidatorResult.success();
        }

        return OAuth2TokenValidatorResult.failure(
                this.createOAuth2Error("User not found!")
        );
    }

    private OAuth2Error createOAuth2Error(String message) {
        return new OAuth2Error(
                "invalid_token",
                message,
                "https://tools.ietf.org/html/rfc6750#section-3.1"
        );
    }
}
