package com.lucasjosino.hawapi.configs;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for endpoints cache
 *
 * @author Lucas Josino
 * @since 1.0.0
 */
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(defaultCache());
        return cacheManager;
    }

    public Caffeine<Object, Object> defaultCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                // Cache will expire after 1 hour of inactivity
                .expireAfterAccess(3600, TimeUnit.SECONDS)
                .recordStats();
    }
}
