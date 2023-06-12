package com.lucasjosino.hawapi.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration for spring:
 * <ul>
 *      <li>Headers</li>
 *      <li>Csrf</li>
 *      <li>Cors</li>
 *      <li>Password encoder</li>
 *      <li>API endpoints</li>
 * </ul>
 *
 * @author Lucas Josino
 * @since 1.0.0
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .headers(req ->
                        req.frameOptions().sameOrigin()
                )
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(req -> req
                        // API Docs
                        .antMatchers(HttpMethod.GET, "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        // Errors
                        .antMatchers(HttpMethod.GET, "/error").permitAll()
                        // Auth
                        .antMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        // API
                        .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .antMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        // Others endpoints
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList(
                "X-Pagination-Page-Index",
                "X-Pagination-Page-Size",
                "X-Pagination-Page-Total",
                "X-Pagination-Item-Total"
        ));
        config.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Bearer"
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PATCH",
                "DELETE"
        ));
        config.addAllowedOriginPattern("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
