package com.lucasjosino.hawapi.configs;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;

public class SpringDocConfig {
    @Bean
    public static OpenAPI springDocsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(APIConfig.title)
                        .description(APIConfig.description)
                        .version(APIConfig.version)
                        .license(new License()
                                .name(APIConfig.license)
                                .url(APIConfig.licenseUrl)
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("HawAPI | Docs")
                        .url("https://hawapi.theproject.id/docs")
                );
    }
}
