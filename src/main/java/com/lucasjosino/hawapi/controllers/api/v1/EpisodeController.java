package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import com.lucasjosino.hawapi.models.dto.translation.EpisodeTranslationDTO;
import com.lucasjosino.hawapi.services.impl.EpisodeServiceImpl;
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
@RequestMapping("/api/v1/episodes")
@Tag(
        name = "Episodes",
        description = "Endpoints for managing episodes",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/episodes"
        )
)
public class EpisodeController implements BaseTranslationInterface<EpisodeDTO, EpisodeTranslationDTO> {

    private final EpisodeServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public EpisodeController(EpisodeServiceImpl service, ResponseUtils responseUtils) {
        this.service = service;
        this.responseUtils = responseUtils;
    }

    @Operation(summary = "Get all episodes")
    public ResponseEntity<List<EpisodeDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                uuids,
                pageable,
                filters.get("language")
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<EpisodeDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @Operation(summary = "Get random episode")
    public ResponseEntity<EpisodeDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    @Operation(summary = "Get all episode translations")
    public ResponseEntity<List<EpisodeTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    @Operation(summary = "Get random episode translation")
    public ResponseEntity<EpisodeTranslationDTO> findRandomTranslation(UUID uuid) {
        return ResponseEntity.ok(service.findRandomTranslation(uuid));
    }

    @Operation(summary = "Get episode")
    public ResponseEntity<EpisodeDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    @Operation(summary = "Get episode translation")
    public ResponseEntity<EpisodeTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    @Operation(summary = "Save episode", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<EpisodeDTO> save(EpisodeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @Operation(summary = "Save episode translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<EpisodeTranslationDTO> saveTranslation(UUID uuid, EpisodeTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    @Operation(summary = "Patch episode", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<EpisodeDTO> patch(UUID uuid, EpisodeDTO patch) {
        try {
            service.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Patch episode translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<EpisodeTranslationDTO> patchTranslation(
            UUID uuid,
            String language,
            EpisodeTranslationDTO dto
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

    @Operation(summary = "Delete episode", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete episode translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
