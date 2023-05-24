package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.interfaces.MappingInterface;
import com.lucasjosino.hawapi.interfaces.TranslationInterface;
import com.lucasjosino.hawapi.models.dto.SeasonDTO;
import com.lucasjosino.hawapi.models.dto.translation.SeasonTranslationDTO;
import com.lucasjosino.hawapi.services.SeasonService;
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

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@RestController
@RequestMapping("/api/v1/seasons")
public class SeasonController implements MappingInterface<SeasonDTO>, TranslationInterface<SeasonTranslationDTO> {

    private final SeasonService service;

    private final ResponseUtils responseUtils;

    @Autowired
    public SeasonController(SeasonService SeasonService, ResponseUtils responseUtils) {
        this.service = SeasonService;
        this.responseUtils = responseUtils;
    }

    @GetMapping
    public ResponseEntity<List<SeasonDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());

        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                filters.get("language"),
                uuids,
                pageable,
                uuids.getContent().size()
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<SeasonDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @GetMapping("/random")
    public ResponseEntity<SeasonDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    @GetMapping("/{uuid}/translations")
    public ResponseEntity<List<SeasonTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SeasonDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    @GetMapping("/{uuid}/translations/{language}")
    public ResponseEntity<SeasonTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    @PostMapping
    public ResponseEntity<SeasonDTO> save(SeasonDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PostMapping("/{uuid}/translations")
    public ResponseEntity<SeasonTranslationDTO> saveTranslation(UUID uuid, SeasonTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    @PatchMapping("/{uuid}")
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

    @PatchMapping("/{uuid}/translations/{language}")
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
