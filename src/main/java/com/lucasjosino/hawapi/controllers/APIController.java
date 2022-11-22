package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.APIModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class APIController {
    @GetMapping
    public String getAPIInfo() {
        return APIModel.name;
    }
}
