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

/**
 * A JWT Manager to handle token creation
 *
 * @author Lucas Josino
 * @see OpenAPIProperty
 * @see UserModel
 * @since 1.0.0
 */
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

    /**
     * Generate a JWT with using:
     * <ul>
     *     <li>Random {@link UUID}</li>
     *     <li>Project name</li>
     *     <li>Request time</li>
     *     <li>Project url</li>
     *     <li>Username</li>
     *     <li>Email</li>
     *     <li>Role</li>
     * </ul>
     *
     * @see OpenAPIProperty
     */
    public String generateToken(UserModel user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(apiProperty.getTitle())
                .issuedAt(now)
                .audience(new ArrayList<String>() {{
                    add(apiProperty.getApiUrl());
                }})
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim(JWT_ROLE_NAME, user.getRole())
                .build();


        JwtEncoderParameters params = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(params).getTokenValue();
    }
}
