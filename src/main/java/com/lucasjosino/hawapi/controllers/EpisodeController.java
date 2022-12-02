package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.services.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/episodes")
public class EpisodeController implements MappingInterface<EpisodeModel> {

    private final EpisodeService episodeService;

    @Autowired
    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    @GetMapping
    public ResponseEntity<List<EpisodeModel>> findAll() {
        return ResponseEntity.ok(episodeService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EpisodeModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(episodeService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<EpisodeModel> save(@RequestBody EpisodeModel episode) {
        return ResponseEntity.ok(episodeService.save(episode));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        episodeService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
