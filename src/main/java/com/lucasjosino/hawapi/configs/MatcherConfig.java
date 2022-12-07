package com.lucasjosino.hawapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ExampleMatcher;

@Configuration
public class MatcherConfig {

    @Bean
    public ExampleMatcher exampleMatcher() {
        return ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase();
    }
}
