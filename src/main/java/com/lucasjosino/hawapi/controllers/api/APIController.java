package com.lucasjosino.hawapi.controllers.api;

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

@RestController
@RequestMapping("/api")
@Tag(
        name = "API",
        description = "Endpoints for managing API",
        externalDocs = @ExternalDocumentation(
                url = "/docs/javadoc/apidocs"
        )
)
public class APIController {

    private final OpenAPIProperty apiConfig;

    @Autowired
    public APIController(OpenAPIProperty apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Operation(summary = "Get API information")
    @ApiResponse(responseCode = "200", description = "Successful")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpenAPIProperty> getAPIInfo() {
        return ResponseEntity.ok(apiConfig);
    }

    @Operation(summary = "Test API")
    @ApiResponse(responseCode = "200", description = "Successful")
    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("Pong");
    }
}
