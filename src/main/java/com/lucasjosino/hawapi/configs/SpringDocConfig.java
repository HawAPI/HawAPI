package com.lucasjosino.hawapi.configs;

import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for spring documentation:
 * <ul>
 *      <li>OpenAPI</li>
 * </ul>
 */
@Configuration
public class SpringDocConfig {

    private final OpenAPIProperty api;

    @Autowired
    public SpringDocConfig(OpenAPIProperty api) {
        this.api = api;
    }

    @Bean
    public OpenAPI springDocsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(api.getTitle())
                        .description(api.getDescription())
                        .version(api.getVersion())
                        .license(new License()
                                .name(api.getLicense())
                                .url(api.getLicenseUrl())
                        )
                )
                .addServersItem(new Server().url(api.getUrl()).description("HawAPI Project"))
                .externalDocs(new ExternalDocumentation()
                        .description(api.getTitle() + " | Docs")
                        .url(api.getDocs())
                );
    }
}
