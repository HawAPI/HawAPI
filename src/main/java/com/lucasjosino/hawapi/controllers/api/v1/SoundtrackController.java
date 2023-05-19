package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.services.SoundtrackService;
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
@RequestMapping("${hawapi.apiBaseUrl}/soundtracks")
public class SoundtrackController implements MappingInterface<SoundtrackDTO> {

    private final SoundtrackService service;

    private final ResponseUtils responseUtils;

    @Autowired
    public SoundtrackController(SoundtrackService SoundtrackService, ResponseUtils responseUtils) {
        this.service = SoundtrackService;
        this.responseUtils = responseUtils;
    }

    @GetMapping
    public ResponseEntity<List<SoundtrackDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                filters.get("language"),
                uuids,
                pageable,
                uuids.getContent().size()
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<SoundtrackDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SoundtrackDTO> findBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findBy(uuid));
    }

    @PostMapping
    public ResponseEntity<SoundtrackDTO> save(SoundtrackDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<SoundtrackDTO> patch(UUID uuid, SoundtrackDTO patch) {
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
