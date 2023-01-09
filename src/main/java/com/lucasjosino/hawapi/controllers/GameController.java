package com.lucasjosino.hawapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.GameFilter;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/games")
public class GameController implements MappingInterface<GameModel, GameFilter> {

    private final GameService gameService;

    @Autowired
    public GameController(GameService GameService) {
        this.gameService = GameService;
    }

    @GetMapping
    public ResponseEntity<List<GameModel>> findAll(GameFilter filter) {
        return ResponseEntity.ok(gameService.findAll(filter));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GameModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(gameService.findByUUID(uuid));
    }

    @PostMapping
    public ResponseEntity<GameModel> save(@RequestBody GameModel episode) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.save(episode));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<Void> patch(@PathVariable UUID uuid, @RequestBody JsonNode patch) {
        try {
            gameService.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        gameService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
