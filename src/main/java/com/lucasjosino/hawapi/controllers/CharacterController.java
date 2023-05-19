package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.services.CharacterService;
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
@RequestMapping("${hawapi.apiBaseUrl}/characters")
public class CharacterController implements MappingInterface<CharacterDTO> {

    private final CharacterService service;

    private final ResponseUtils responseUtils;

    @Autowired
    public CharacterController(CharacterService service, ResponseUtils responseUtils) {
        this.service = service;
        this.responseUtils = responseUtils;
    }

    @GetMapping
    public ResponseEntity<List<CharacterDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                filters.get("language"),
                uuids,
                pageable,
                uuids.getContent().size()
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<CharacterDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CharacterDTO> findBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findBy(uuid));
    }

    @PostMapping
    public ResponseEntity<CharacterDTO> save(CharacterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<CharacterDTO> patch(UUID uuid, CharacterDTO patch) {
        try {
            service.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
