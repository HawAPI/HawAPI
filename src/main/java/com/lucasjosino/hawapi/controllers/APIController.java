package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("${hawapi.apiPath}")
public class APIController {

    private final List<String> endpoints;

    private final OpenAPIConfig apiConfig;

    private final RequestMappingHandlerMapping mappingHandler;

    @Autowired
    public APIController(List<String> endpoints, OpenAPIConfig apiConfig, RequestMappingHandlerMapping mappingHandler) {
        this.endpoints = endpoints;
        this.apiConfig = apiConfig;
        this.mappingHandler = mappingHandler;
    }

    @GetMapping
    public ResponseEntity<OpenAPIConfig> getAPIInfo() {
        return ResponseEntity.ok(apiConfig);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("Pong");
    }

    @GetMapping("/endpoints")
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
