package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseControllerInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.services.impl.CharacterServiceImpl;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/characters")
@Tag(
        name = "Characters",
        description = "Endpoints for managing characters",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/characters"
        )
)
public class CharacterController implements BaseControllerInterface<CharacterDTO> {

    private final CharacterServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public CharacterController(CharacterServiceImpl service, ResponseUtils responseUtils) {
        this.service = service;
        this.responseUtils = responseUtils;
    }

    @Operation(summary = "Get all characters")
    public ResponseEntity<List<CharacterDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                uuids,
                pageable,
                null
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<CharacterDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @Operation(summary = "Get random character")
    public ResponseEntity<CharacterDTO> findRandom(String language) {
        return ResponseEntity.ok().body(service.findRandom(language));
    }

    @Operation(summary = "Get character")
    public ResponseEntity<CharacterDTO> findBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findBy(uuid, language));
    }

    @Operation(summary = "Save character", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<CharacterDTO> save(CharacterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @Operation(summary = "Patch character", security = @SecurityRequirement(name = "Bearer"))
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

    @Operation(summary = "Delete character", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
