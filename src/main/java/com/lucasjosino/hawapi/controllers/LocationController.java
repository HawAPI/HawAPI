package com.lucasjosino.hawapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.LocationFilter;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/places")
public class LocationController implements MappingInterface<LocationModel, LocationFilter> {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService LocationService) {
        this.locationService = LocationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationModel>> findAll(LocationFilter filter) {
        return ResponseEntity.ok(locationService.findAll(filter));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<LocationModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(locationService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<LocationModel> save(@RequestBody LocationModel episode) {
        return ResponseEntity.ok(locationService.save(episode));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<Void> patch(@PathVariable UUID uuid, @RequestBody JsonNode patch) {
        try {
            locationService.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        locationService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
