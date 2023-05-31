package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.SeasonDTO;
import com.lucasjosino.hawapi.models.dto.translation.SeasonTranslationDTO;
import com.lucasjosino.hawapi.services.impl.SeasonServiceImpl;
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
@RequestMapping("/api/v1/seasons")
@Tag(
        name = "Seasons",
        description = "Endpoints for managing seasons",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/seasons"
        )
)
public class SeasonController implements BaseTranslationInterface<SeasonDTO, SeasonTranslationDTO> {

    private final SeasonServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public SeasonController(SeasonServiceImpl SeasonService, ResponseUtils responseUtils) {
        this.service = SeasonService;
        this.responseUtils = responseUtils;
    }

    @Operation(summary = "Get all seasons")
    public ResponseEntity<List<SeasonDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                uuids,
                pageable,
                filters.get("language")
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<SeasonDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @Operation(summary = "Get random season")
    public ResponseEntity<SeasonDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    @Operation(summary = "Get all season translations")
    public ResponseEntity<List<SeasonTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    @Operation(summary = "Get random season translation")
    public ResponseEntity<SeasonTranslationDTO> findRandomTranslation(UUID uuid) {
        return ResponseEntity.ok(service.findRandomTranslation(uuid));
    }

    @Operation(summary = "Get season")
    public ResponseEntity<SeasonDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    @Operation(summary = "Get season translation")
    public ResponseEntity<SeasonTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    @Operation(summary = "Save season", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonDTO> save(SeasonDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @Operation(summary = "Save season translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonTranslationDTO> saveTranslation(UUID uuid, SeasonTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    @Operation(summary = "Patch season", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonDTO> patch(UUID uuid, SeasonDTO patch) {
        try {
            service.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Patch season translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonTranslationDTO> patchTranslation(
            UUID uuid,
            String language,
            SeasonTranslationDTO dto
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

    @Operation(summary = "Delete season", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete season translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
