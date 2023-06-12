package com.lucasjosino.hawapi.configs;

import com.lucasjosino.hawapi.cache.generator.FindAllKeyGenerator;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for endpoints cache
 */
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public KeyGenerator findAllKeyGenerator() {
        return new FindAllKeyGenerator();
    }
}
