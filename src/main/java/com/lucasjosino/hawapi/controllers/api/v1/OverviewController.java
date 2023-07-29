package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseAPIControllerInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.core.StringUtils;
import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.services.impl.OverviewServiceImpl;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "Overview",
        description = "Endpoints for managing API overview",
        externalDocs = @ExternalDocumentation(
                url = "/docs"
        )
)
public class OverviewController implements BaseAPIControllerInterface {

    private final OverviewServiceImpl service;

    private final List<String> endpoints;

    private final OpenAPIProperty apiConfig;

    private final ResponseUtils responseUtils;

    private final RequestMappingHandlerMapping mappingHandler;

    @Autowired
    public OverviewController(
            OverviewServiceImpl service,
            List<String> endpoints,
            OpenAPIProperty apiConfig,
            ResponseUtils responseUtils,
            RequestMappingHandlerMapping mappingHandler
    ) {
        this.service = service;
        this.endpoints = endpoints;
        this.apiConfig = apiConfig;
        this.responseUtils = responseUtils;
        this.mappingHandler = mappingHandler;
    }

    @Operation(summary = "Get all API endpoints")
    public ResponseEntity<List<String>> getEndpoints() {
        return ResponseEntity.ok(endpoints);
    }

    @Operation(summary = "Get API overview")
    public ResponseEntity<OverviewDTO> getOverview(String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            language = responseUtils.getDefaultLanguage();
        }

        return ResponseEntity.ok(service.getOverviewByLanguage(language));
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
