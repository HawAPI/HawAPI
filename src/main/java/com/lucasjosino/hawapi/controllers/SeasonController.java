package com.lucasjosino.hawapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SeasonFilter;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.services.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/seasons")
public class SeasonController implements MappingInterface<SeasonModel, SeasonFilter> {

    private final SeasonService seasonService;

    @Autowired
    public SeasonController(SeasonService SeasonService) {
        this.seasonService = SeasonService;
    }

    @GetMapping
    public ResponseEntity<List<SeasonModel>> findAll(SeasonFilter filter) {
        return ResponseEntity.ok(seasonService.findAll(filter));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SeasonModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(seasonService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<SeasonModel> save(@RequestBody SeasonModel episode) {
        return ResponseEntity.ok(seasonService.save(episode));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<Void> patch(@PathVariable UUID uuid, @RequestBody JsonNode patch) {
        try {
            seasonService.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        seasonService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
