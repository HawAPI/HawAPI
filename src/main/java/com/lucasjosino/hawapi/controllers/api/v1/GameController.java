package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.models.dto.GameDTO;
import com.lucasjosino.hawapi.models.dto.translation.GameTranslationDTO;
import com.lucasjosino.hawapi.services.impl.GameServiceImpl;
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

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@RestController
@RequestMapping("/api/v1/games")
@Tag(
        name = "Games",
        description = "Endpoints for managing games",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/games"
        )
)
public class GameController implements BaseTranslationInterface<GameDTO, GameTranslationDTO> {

    private final GameServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public GameController(GameServiceImpl GameService, ResponseUtils responseUtils) {
        this.service = GameService;
        this.responseUtils = responseUtils;
    }

    @Operation(summary = "Get all games")
    public ResponseEntity<List<GameDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                uuids,
                pageable,
                filters.get("language")
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<GameDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @Operation(summary = "Get random game")
    public ResponseEntity<GameDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    @Operation(summary = "Get all game translations")
    public ResponseEntity<List<GameTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    @Operation(summary = "Get random game translation")
    public ResponseEntity<GameTranslationDTO> findRandomTranslation(UUID uuid) {
        return ResponseEntity.ok(service.findRandomTranslation(uuid));
    }

    @Operation(summary = "Get game")
    public ResponseEntity<GameDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    @Operation(summary = "Get game translation")
    public ResponseEntity<GameTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    @Operation(summary = "Save game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameDTO> save(GameDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @Operation(summary = "Patch game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameTranslationDTO> saveTranslation(UUID uuid, GameTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    @Operation(summary = "Patch game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameDTO> patch(UUID uuid, GameDTO patch) {
        try {
            service.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Patch game translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameTranslationDTO> patchTranslation(
            UUID uuid,
            String language,
            GameTranslationDTO dto
    ) {
        try {
            service.patchTranslation(uuid, language, dto);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete game translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
