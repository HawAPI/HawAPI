package com.lucasjosino.hawapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class HawAPIApplication {
    public static void main(String[] args) {
        SpringApplication.run(HawAPIApplication.class, args);
    }
}
