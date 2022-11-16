package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.APIModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class APIController {

    public APIModel apiModel = new APIModel(
            "HawAPI",
            "A free and open source API for Stranger Things.",
            "v1",
            "https://github.com/LucJosin/HawAPI",
            "https://hawapi.theproject.id",
            "https://hawapi.theproject.id/api/v1",
            "/api/v1"
    );

    @GetMapping()
    public APIModel getAPIInfo() {
        return apiModel;
    }
}
