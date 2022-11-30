package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${hawapi.apiPath}")
public class APIController {

    private final OpenAPIConfig apiDtoModel;

    @Autowired
    public APIController(OpenAPIConfig apiDtoModel) {
        this.apiDtoModel = apiDtoModel;
    }

    @GetMapping
    public ResponseEntity<OpenAPIConfig> getAPIInfo() {
        return ResponseEntity.ok(apiDtoModel);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("Pong");
    }
}
