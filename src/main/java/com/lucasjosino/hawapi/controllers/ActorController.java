package com.lucasjosino.hawapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.services.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/actors")
public class ActorController implements MappingInterface<ActorModel> {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public ResponseEntity<List<ActorModel>> findAll() {
        return ResponseEntity.ok(actorService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ActorModel> findByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(actorService.findById(uuid));
    }

    @PostMapping
    public ResponseEntity<ActorModel> save(@RequestBody ActorModel actor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.save(actor));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<Void> patch(@PathVariable UUID uuid, @RequestBody JsonNode patch) {
        try {
            actorService.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        actorService.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
