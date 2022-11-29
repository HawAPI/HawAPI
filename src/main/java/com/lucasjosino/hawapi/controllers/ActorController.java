package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.services.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${apiPath}/${apiVersion}/actors")
public class ActorController {

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
    public ResponseEntity<ActorModel> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(actorService.findById(uuid));
    }

    @PostMapping
    public ResponseEntity<ActorModel> save(@RequestBody ActorModel actor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.save(actor));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        actorService.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
