package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(
        name = "API",
        description = "Endpoints for managing API",
        externalDocs = @ExternalDocumentation(
                url = "/docs/guides/authentication"
        )
)
public class APIController {

    private final List<String> endpoints;

    private final OpenAPIProperty apiConfig;

    private final RequestMappingHandlerMapping mappingHandler;

    @Autowired
    public APIController(
            List<String> endpoints,
            OpenAPIProperty apiConfig,
            RequestMappingHandlerMapping mappingHandler
    ) {
        this.endpoints = endpoints;
        this.apiConfig = apiConfig;
        this.mappingHandler = mappingHandler;
    }

    @Operation(summary = "Get API information")
    @ApiResponse(responseCode = "200", description = "Successful")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpenAPIProperty> getAPIInfo() {
        return ResponseEntity.ok(apiConfig);
    }

    @Operation(summary = "Get pong / Test API")
    @ApiResponse(responseCode = "200", description = "Successful")
    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("Pong");
    }

    @Operation(summary = "Get all API endpoints")
    @ApiResponse(responseCode = "200", description = "Successful")
    @GetMapping(value = "/endpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getEndpoints() {
        return ResponseEntity.ok(endpoints);
    }

    @PostConstruct
    public void findAllAPIEndpoints() {
        mappingHandler.getHandlerMethods().forEach((request, v) -> {
            // Check if the endpoint is a GET request.
            if (request.getMethodsCondition().toString().contains("GET")) {
                // Get the first item from Set.
                String value = request.getPatternValues().stream().findFirst().orElse(null);
                // Check if the endpoint is from the API.
                if (value != null && value.contains(apiConfig.getApiBaseUrl())) {
                    endpoints.add(value);
                }
            }
        });

        // Sort(alphabetically) this endpoint list.
        Collections.sort(endpoints);
    }
}
