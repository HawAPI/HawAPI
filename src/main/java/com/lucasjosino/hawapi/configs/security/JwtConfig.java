package com.lucasjosino.hawapi.configs.security;

import com.lucasjosino.hawapi.jwt.JwtAudienceValidator;
import com.lucasjosino.hawapi.jwt.JwtManager;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.properties.RsaKeysProperty;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class JwtConfig {

    private final OpenAPIProperty apiProperty;

    private final RsaKeysProperty rsaKeysProperty;

    public JwtConfig(OpenAPIProperty apiProperty, RsaKeysProperty rsaKeysProperty) {
        this.apiProperty = apiProperty;
        this.rsaKeysProperty = rsaKeysProperty;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaKeysProperty.getPublicKey()).build();
        decoder.setJwtValidator(tokenValidator());
        return decoder;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeysProperty.getPublicKey())
                .privateKey(rsaKeysProperty.getPrivateKey())
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName(JwtManager.JWT_ROLE_NAME);
        grantedAuthoritiesConverter.setAuthorityPrefix(JwtManager.ROLE_PREFIX);

        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    public OAuth2TokenValidator<Jwt> tokenValidator() {
        List<OAuth2TokenValidator<Jwt>> validators =
                Arrays.asList(
                        new JwtTimestampValidator(),
                        new JwtIssuerValidator(apiProperty.getTitle()),
                        new JwtAudienceValidator(apiProperty.getApiUrl())
                );
        return new DelegatingOAuth2TokenValidator<>(validators);
    }
}
