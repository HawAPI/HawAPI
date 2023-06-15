package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.LocationFilter;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Endpoints for managing locations
 *
 * @author Lucas Josino
 * @see <a href="https://hawapi.theproject.id/docs/api/locations">HawAPI#Locations</a>
 * @since 1.0.0
 */
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

    /**
     * Method that get all locations
     *
     * @param filters  An {@link Map} of params that represents {@link LocationFilter}. Can be empty
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link List} of {@link LocationDTO} or empty
     * @throws InternalServerErrorException If a valid filter is not provided
     * @since 1.0.0
     */
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

    /**
     * Method that get a single random location
     *
     * @return An single {@link LocationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random location")
    public ResponseEntity<LocationDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    /**
     * Method that get all location translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return A {@link List} of {@link LocationTranslationDTO} or empty
     * @throws ItemNotFoundException If item with <strong>uuid</strong> doesn't exist
     * @since 1.0.0
     */
    @Operation(summary = "Get all location translations")
    public ResponseEntity<List<LocationTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    /**
     * Method that get a single random location translation
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An single {@link LocationTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random location translation")
    public ResponseEntity<LocationTranslationDTO> findRandomTranslation(UUID uuid) {
        return ResponseEntity.ok(service.findRandomTranslation(uuid));
    }

    /**
     * Method that get a single location
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link LocationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get location")
    public ResponseEntity<LocationDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    /**
     * Method that get a single location translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link LocationTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get location translation")
    public ResponseEntity<LocationTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    /**
     * Method that crates a location
     *
     * @param dto An {@link LocationDTO} with all location fields
     * @return An {@link LocationDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @since 1.0.0
     */
    @Operation(summary = "Save location", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationDTO> save(LocationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    /**
     * Method that crates a location translation
     *
     * @param dto An {@link LocationDTO} with all location fields
     * @return An {@link LocationTranslationDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Save location translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationTranslationDTO> saveTranslation(UUID uuid, LocationTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    /**
     * Method that updates a location
     *
     * @param uuid  An {@link UUID} that represents a specific item
     * @param patch An {@link LocationDTO} with updated location fields
     * @return An {@link LocationDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Patch location", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationDTO> patch(UUID uuid, LocationDTO patch) throws IOException {
        // All translation models will be queried with 'eager' type.
        // If 'language' is not provided, query will return more than one result, resulting in an error.
        patch.setLanguage(responseUtils.getDefaultLanguage());

        service.patch(uuid, patch);
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that updates a location translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @param patch    An {@link LocationTranslationDTO} with updated location fields
     * @return An {@link LocationTranslationDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @throws SaveConflictException If uuid is invalid
     * @since 1.0.0
     */
    @Operation(summary = "Patch location translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<LocationTranslationDTO> patchTranslation(
            UUID uuid,
            String language,
            LocationTranslationDTO patch
    ) throws IOException {
        service.patchTranslation(uuid, language, patch);
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that delete a location and all translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete location", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Method that delete a location translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete location translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
