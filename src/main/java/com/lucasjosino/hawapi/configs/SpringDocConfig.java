package com.lucasjosino.hawapi.configs;

import com.lucasjosino.hawapi.models.dto.OpenAPIConfig;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    private final OpenAPIConfig api;

    @Autowired
    public SpringDocConfig(OpenAPIConfig api) {
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
                .externalDocs(new ExternalDocumentation()
                        .description("HawAPI | Docs")
                        .url("https://hawapi.theproject.id/docs")
                );
    }
}
