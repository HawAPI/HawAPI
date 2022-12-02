package com.lucasjosino.hawapi.controllers;

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
public class SeasonController implements MappingInterface<SeasonModel> {

    private final SeasonService seasonService;

    @Autowired
    public SeasonController(SeasonService SeasonService) {
        this.seasonService = SeasonService;
    }

    @GetMapping
    public ResponseEntity<List<SeasonModel>> findAll() {
        return ResponseEntity.ok(seasonService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SeasonModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(seasonService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<SeasonModel> save(@RequestBody SeasonModel episode) {
        return ResponseEntity.ok(seasonService.save(episode));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        seasonService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
