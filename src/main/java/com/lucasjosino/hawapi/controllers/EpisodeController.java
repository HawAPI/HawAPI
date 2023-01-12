package com.lucasjosino.hawapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.services.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/episodes")
public class EpisodeController implements MappingInterface<EpisodeModel, EpisodeFilter> {

    private final EpisodeService episodeService;

    @Autowired
    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    @GetMapping
    public ResponseEntity<List<EpisodeModel>> findAll(EpisodeFilter filter) {
        return ResponseEntity.ok(episodeService.findAll(filter));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EpisodeModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(episodeService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<EpisodeModel> save(@RequestBody EpisodeModel episode) {
        return ResponseEntity.status(HttpStatus.CREATED).body(episodeService.save(episode));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<Void> patch(@PathVariable UUID uuid, @RequestBody JsonNode patch) {
        try {
            episodeService.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        episodeService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
