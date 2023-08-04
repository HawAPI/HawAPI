package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.SeasonFilter;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Endpoints for managing seasons
 *
 * @author Lucas Josino
 * @see <a href="https://hawapi.theproject.id/docs/api/seasons">HawAPI#Seasons</a>
 * @since 1.0.0
 */
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

    /**
     * Method that get all seasons
     *
     * @param filters  An {@link Map} of params that represents {@link SeasonFilter}. Can be empty
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link List} of {@link SeasonDTO} or empty
     * @throws InternalServerErrorException If a valid filter is not provided
     * @since 1.0.0
     */
    @Operation(summary = "Get all seasons")
    public ResponseEntity<List<SeasonDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        pageable = responseUtils.validateSort(pageable);

        Page<UUID> uuids = service.findAllUUIDs(filters, pageable);
        List<SeasonDTO> res = service.findAll(filters, uuids);
        HttpHeaders headers = responseUtils.getHeaders(uuids, null);

        return ResponseEntity.ok().headers(headers).body(res);
    }

    /**
     * Method that get a single random season
     *
     * @return An single {@link SeasonDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random season")
    public ResponseEntity<SeasonDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    /**
     * Method that get all season translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return A {@link List} of {@link SeasonTranslationDTO} or empty
     * @throws ItemNotFoundException If item with <strong>uuid</strong> doesn't exist
     * @since 1.0.0
     */
    @Operation(summary = "Get all season translations")
    public ResponseEntity<List<SeasonTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    /**
     * Method that get a single random season translation
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An single {@link SeasonTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random season translation")
    public ResponseEntity<SeasonTranslationDTO> findRandomTranslation(UUID uuid) {
        SeasonTranslationDTO translation = service.findRandomTranslation(uuid);
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.ok().headers(headers).body(translation);
    }

    /**
     * Method that get a single season
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link SeasonDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get season")
    public ResponseEntity<SeasonDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    /**
     * Method that get a single season translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link SeasonTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get season translation")
    public ResponseEntity<SeasonTranslationDTO> findTranslationBy(UUID uuid, String language) {
        SeasonTranslationDTO translation = service.findTranslationBy(uuid, language);
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.ok().headers(headers).body(translation);
    }

    /**
     * Method that crates a season
     *
     * @param dto An {@link SeasonDTO} with all season fields
     * @return An {@link SeasonDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @since 1.0.0
     */
    @Operation(summary = "Save season", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonDTO> save(SeasonDTO dto) {
        SeasonDTO episode = service.save(dto);
        HttpHeaders headers = responseUtils.getHeaders(episode.getLanguage());

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(episode);
    }

    /**
     * Method that crates a season translation
     *
     * @param dto An {@link SeasonDTO} with all season fields
     * @return An {@link SeasonTranslationDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Save season translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonTranslationDTO> saveTranslation(UUID uuid, SeasonTranslationDTO dto) {
        SeasonTranslationDTO translation = service.saveTranslation(uuid, dto);
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(translation);
    }

    /**
     * Method that updates a season
     *
     * @param uuid  An {@link UUID} that represents a specific item
     * @param patch An {@link SeasonDTO} with updated season fields
     * @return An {@link SeasonDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Patch season", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonDTO> patch(UUID uuid, SeasonDTO patch) throws IOException {
        // All translation models will be queried with 'eager' type.
        // If 'language' is not provided, query will return more than one result, resulting in an error.
        patch.setLanguage(responseUtils.getDefaultLanguage());

        service.patch(uuid, patch);
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that updates a season translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @param patch    An {@link SeasonTranslationDTO} with updated season fields
     * @return An {@link SeasonTranslationDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @throws SaveConflictException If uuid is invalid
     * @since 1.0.0
     */
    @Operation(summary = "Patch season translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<SeasonTranslationDTO> patchTranslation(
            UUID uuid,
            String language,
            SeasonTranslationDTO patch
    ) throws IOException {
        service.patchTranslation(uuid, language, patch);
        HttpHeaders headers = responseUtils.getHeaders(language);

        return ResponseEntity.ok().headers(headers).body(patch);
    }

    /**
     * Method that delete a season and all translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete season", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Method that delete a season translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete season translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
