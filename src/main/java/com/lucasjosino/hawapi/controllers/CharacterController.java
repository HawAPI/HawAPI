package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.services.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("{apiPath}/{apiVersion}/characters")
public class CharacterController {

    private final CharacterService characterService;

    @Autowired
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping
    public ResponseEntity<List<CharacterModel>> findAll() {
        return ResponseEntity.ok(characterService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CharacterModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(characterService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<CharacterModel> save(@RequestBody CharacterModel character) {
        return ResponseEntity.status(HttpStatus.CREATED).body(characterService.save(character));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        characterService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
