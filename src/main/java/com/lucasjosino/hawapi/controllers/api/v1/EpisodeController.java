package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
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

/**
 * Endpoints for managing episodes
 *
 * @author Lucas Josino
 * @see <a href="https://hawapi.theproject.id/docs/api/episodes">HawAPI#Episodes</a>
 * @since 1.0.0
 */
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

    /**
     * Method that get all episodes
     *
     * @param filters  An {@link Map} of params that represents {@link EpisodeFilter}. Can be empty
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link List} of {@link EpisodeDTO} or empty
     * @throws InternalServerErrorException If a valid filter is not provided
     * @since 1.0.0
     */
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

    /**
     * Method that get a single random episode
     *
     * @return An single {@link EpisodeDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random episode")
    public ResponseEntity<EpisodeDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    /**
     * Method that get all episode translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return A {@link List} of {@link EpisodeTranslationDTO} or empty
     * @throws ItemNotFoundException If item with <strong>uuid</strong> doesn't exist
     * @since 1.0.0
     */
    @Operation(summary = "Get all episode translations")
    public ResponseEntity<List<EpisodeTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    /**
     * Method that get a single random episode translation
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An single {@link EpisodeTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random episode translation")
    public ResponseEntity<EpisodeTranslationDTO> findRandomTranslation(UUID uuid) {
        return ResponseEntity.ok(service.findRandomTranslation(uuid));
    }

    /**
     * Method that get a single episode
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link EpisodeDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get episode")
    public ResponseEntity<EpisodeDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    /**
     * Method that get a single episode translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link EpisodeTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get episode translation")
    public ResponseEntity<EpisodeTranslationDTO> findTranslationBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findTranslationBy(uuid, language));
    }

    /**
     * Method that crates an episode
     *
     * @param dto An {@link EpisodeDTO} with all episode fields
     * @return An {@link EpisodeDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @since 1.0.0
     */
    @Operation(summary = "Save episode", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<EpisodeDTO> save(EpisodeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    /**
     * Method that crates an episode translation
     *
     * @param dto An {@link EpisodeDTO} with all episode fields
     * @return An {@link EpisodeTranslationDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Save episode translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<EpisodeTranslationDTO> saveTranslation(UUID uuid, EpisodeTranslationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTranslation(uuid, dto));
    }

    /**
     * Method that updates an episode
     *
     * @param uuid  An {@link UUID} that represents a specific item
     * @param patch An {@link EpisodeDTO} with updated episode fields
     * @return An {@link EpisodeDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @since 1.0.0
     */
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

    /**
     * Method that updates an episode translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @param dto      An {@link EpisodeTranslationDTO} with updated episode fields
     * @return An {@link EpisodeTranslationDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @since 1.0.0
     */
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

    /**
     * Method that delete an episode and all translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete episode", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Method that delete an episode translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete episode translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
