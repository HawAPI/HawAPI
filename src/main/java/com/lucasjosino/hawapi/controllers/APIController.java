package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.dto.OpenAPIDtoModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPath}/")
public class APIController {
    @GetMapping
    public ResponseEntity<OpenAPIDtoModel> getAPIInfo() {
        OpenAPIDtoModel openAPIDto = new OpenAPIDtoModel();
        return ResponseEntity.ok(openAPIDto);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("Pong");
    }
}
