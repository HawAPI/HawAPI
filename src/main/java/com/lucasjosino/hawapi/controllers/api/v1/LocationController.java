package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.models.dto.LocationDTO;
import com.lucasjosino.hawapi.models.dto.translation.LocationTranslationDTO;
import com.lucasjosino.hawapi.services.impl.LocationServiceImpl;
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
@RequestMapping("/api/v1/locations")
@Tag(
        name = "Locations",
        description = "Endpoints for managing locations",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/locations"
        )
)
public class LocationController implements BaseTranslationInterface<LocationDTO, LocationTranslationDTO> {

    private final LocationServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public LocationController(LocationServiceImpl LocationService, ResponseUtils responseUtils) {
        this.service = LocationService;
        this.responseUtils = responseUtils;
    }

    @Operation(summary = "Get all locations")
    public ResponseEntity<List<LocationDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                uuids,
                pageable,
                filters.get("language")
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<LocationDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @Operation(summary = "Get random location")
    public ResponseEntity<LocationDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    @Operation(summary = "Get all location translations")
    public ResponseEntity<List<LocationTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    @Operation(summary = "Get random location translation")
    public ResponseEntity<LocationTranslationDTO> findRandomTranslation(UUID uuid) {
        return ResponseEntity.ok(service.findRandomTranslation(uuid));
    }

    @Operation(summary = "Get location")
    public ResponseEntity<LocationDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    @Operation(summary = "Get location translation")
    public ResponseEntity<LocationTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    @Operation(summary = "Save location", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationDTO> save(LocationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @Operation(summary = "Save location translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationTranslationDTO> saveTranslation(UUID uuid, LocationTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    @Operation(summary = "Patch location", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationDTO> patch(UUID uuid, LocationDTO patch) {
        try {
            service.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Patch location translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationTranslationDTO> patchTranslation(
            UUID uuid,
            String language,
            LocationTranslationDTO dto
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

    @Operation(summary = "Delete location", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete location translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
