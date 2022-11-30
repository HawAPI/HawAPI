package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.PlaceModel;
import com.lucasjosino.hawapi.services.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/places")
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService PlaceService) {
        this.placeService = PlaceService;
    }

    @GetMapping
    public ResponseEntity<List<PlaceModel>> findAll() {
        return ResponseEntity.ok(placeService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<PlaceModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(placeService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<PlaceModel> save(@RequestBody PlaceModel episode) {
        return ResponseEntity.ok(placeService.save(episode));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        placeService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
