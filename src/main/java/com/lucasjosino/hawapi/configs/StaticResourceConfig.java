package com.lucasjosino.hawapi.configs;

import com.lucasjosino.hawapi.resolvers.IndexFallbackResourceResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for spring static resources:
 * <ul>
 *      <li>Static files</li>
 * </ul>
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .setOrder(Ordered.LOWEST_PRECEDENCE)
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new IndexFallbackResourceResolver());
    }
}
