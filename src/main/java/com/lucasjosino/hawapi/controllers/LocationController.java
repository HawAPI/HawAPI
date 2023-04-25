package com.lucasjosino.hawapi.controllers;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.interfaces.TranslationInterface;
import com.lucasjosino.hawapi.models.dto.LocationDTO;
import com.lucasjosino.hawapi.models.dto.translation.LocationTranslationDTO;
import com.lucasjosino.hawapi.services.LocationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${hawapi.apiBaseUrl}/locations")
public class LocationController implements MappingInterface<LocationDTO>, TranslationInterface<LocationTranslationDTO> {

    private final LocationService service;

    private final ResponseUtils responseUtils;

    @Autowired
    public LocationController(LocationService LocationService, ResponseUtils responseUtils) {
        this.service = LocationService;
        this.responseUtils = responseUtils;
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                filters.get("language"),
                uuids,
                pageable,
                uuids.getSize()
        );

        List<LocationDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @GetMapping("/{uuid}/translations")
    public ResponseEntity<List<LocationTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<LocationDTO> findBy(UUID uuid, String language) {
        language = StringUtils.defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    @GetMapping("/{uuid}/translations/{language}")
    public ResponseEntity<LocationTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> save(LocationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PostMapping("/{uuid}/translations")
    public ResponseEntity<LocationTranslationDTO> saveTranslation(UUID uuid, LocationTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    @PatchMapping("/{uuid}")
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

    @PatchMapping("/{uuid}/translations/{language}")
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

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}/translations/{language}")
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
