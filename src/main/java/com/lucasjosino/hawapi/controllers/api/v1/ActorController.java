package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.dto.ActorDTO;
import com.lucasjosino.hawapi.services.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/actors")
public class ActorController implements MappingInterface<ActorDTO> {

    private final ActorService service;

    private final ResponseUtils responseUtils;

    @Autowired
    public ActorController(ActorService service, ResponseUtils responseUtils) {
        this.service = service;
        this.responseUtils = responseUtils;
    }

    @GetMapping
    public ResponseEntity<List<ActorDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                filters.get("language"),
                uuids,
                pageable,
                uuids.getContent().size()
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<ActorDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @GetMapping("/random")
    public ResponseEntity<ActorDTO> findRandom(String language) {
        return ResponseEntity.ok().body(service.findRandom());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ActorDTO> findBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findBy(uuid));
    }

    @PostMapping
    public ResponseEntity<ActorDTO> save(ActorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<ActorDTO> patch(UUID uuid, ActorDTO patch) {
        try {
            service.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(patch);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
