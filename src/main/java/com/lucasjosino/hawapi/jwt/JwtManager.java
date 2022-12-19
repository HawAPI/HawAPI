package com.lucasjosino.hawapi.jwt;

import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class JwtManager {

    public static final String JWT_ROLE_NAME = "role";

    public static final String ROLE_PREFIX = "ROLE_";

    private final JwtEncoder jwtEncoder;

    private final OpenAPIProperty apiProperty;

    public JwtManager(JwtEncoder jwtEncoder, OpenAPIProperty apiProperty) {
        this.jwtEncoder = jwtEncoder;
        this.apiProperty = apiProperty;
    }

    public String generateToken(UserModel user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(apiProperty.getTitle())
                .issuedAt(now)
                .audience(new ArrayList<String>() {{
                    add(apiProperty.getApiUrl());
                }})
                .claim("nickname", user.getNickname())
                .claim("email", user.getEmail())
                .claim(JWT_ROLE_NAME, user.getRole())
                .build();


        JwtEncoderParameters params = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(params).getTokenValue();
    }
}
