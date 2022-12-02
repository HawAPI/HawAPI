package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/places")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService LocationService) {
        this.locationService = LocationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationModel>> findAll() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<LocationModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(locationService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<LocationModel> save(@RequestBody LocationModel episode) {
        return ResponseEntity.ok(locationService.save(episode));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        locationService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
