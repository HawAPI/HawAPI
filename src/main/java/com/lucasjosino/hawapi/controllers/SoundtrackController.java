package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.services.SoundtrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/soundtracks")
public class SoundtrackController {

    private final SoundtrackService soundtrackService;

    @Autowired
    public SoundtrackController(SoundtrackService SoundtrackService) {
        this.soundtrackService = SoundtrackService;
    }

    @GetMapping
    public ResponseEntity<List<SoundtrackModel>> findAll() {
        return ResponseEntity.ok(soundtrackService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SoundtrackModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(soundtrackService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<SoundtrackModel> save(@RequestBody SoundtrackModel episode) {
        return ResponseEntity.ok(soundtrackService.save(episode));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        soundtrackService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
