package com.lucasjosino.hawapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableCaching
@EnableWebSecurity
@EnableJpaRepositories
@SpringBootApplication
@PropertySource({"classpath:bucket4j.properties"})
public class HawAPIApplication {
    public static void main(String[] args) {
        SpringApplication.run(HawAPIApplication.class, args);
    }
}
