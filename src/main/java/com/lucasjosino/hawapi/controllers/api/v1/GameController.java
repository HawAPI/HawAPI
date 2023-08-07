package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseTranslationInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.GameFilter;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Endpoints for managing games
 *
 * @author Lucas Josino
 * @see <a href="https://hawapi.theproject.id/docs/api/games">HawAPI#Games</a>
 * @since 1.0.0
 */
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

    /**
     * Method that get all games
     *
     * @param filters  An {@link Map} of params that represents {@link GameFilter}. Can be empty
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link List} of {@link GameDTO} or empty
     * @throws InternalServerErrorException If a valid filter is not provided
     * @since 1.0.0
     */
    @Operation(summary = "Get all games")
    public ResponseEntity<List<GameDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        filters.putIfAbsent("language", responseUtils.getDefaultLanguage());
        pageable = responseUtils.validateSort(pageable);

        Page<UUID> uuids = service.findAllUUIDs(filters, pageable);
        List<GameDTO> res = service.findAll(filters.get("language"), uuids);
        HttpHeaders headers = responseUtils.getHeaders(uuids, filters.get("language"));

        return ResponseEntity.ok().headers(headers).body(res);
    }

    /**
     * Method that get a single random game
     *
     * @return An single {@link GameDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random game")
    public ResponseEntity<GameDTO> findRandom(String language) {
        if (isNullOrEmpty(language)) language = responseUtils.getDefaultLanguage();

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findRandom(language));
    }

    /**
     * Method that get all game translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return A {@link List} of {@link GameTranslationDTO} or empty
     * @throws ItemNotFoundException If item with <strong>uuid</strong> doesn't exist
     * @since 1.0.0
     */
    @Operation(summary = "Get all game translations")
    public ResponseEntity<List<GameTranslationDTO>> findAllTranslationsBy(UUID uuid) {
        return ResponseEntity.ok(service.findAllTranslationsBy(uuid));
    }

    /**
     * Method that get a single random game translation
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An single {@link GameTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random game translation")
    public ResponseEntity<GameTranslationDTO> findRandomTranslation(UUID uuid) {
        GameTranslationDTO translation = service.findRandomTranslation(uuid);
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.ok().headers(headers).body(translation);
    }

    /**
     * Method that get a single game
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link GameDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get game")
    public ResponseEntity<GameDTO> findBy(UUID uuid, String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        HttpHeaders headers = responseUtils.getHeaders(language);
        return ResponseEntity.ok().headers(headers).body(service.findBy(uuid, language));
    }

    /**
     * Method that get a single game translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An single {@link GameTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get game translation")
    public ResponseEntity<GameTranslationDTO> findTranslationBy(UUID uuid, String language) {
        GameTranslationDTO translation = service.findTranslationBy(uuid, language);
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.ok().headers(headers).body(translation);
    }

    /**
     * Method that crates a game
     *
     * @param dto An {@link GameDTO} with all game fields
     * @return An {@link GameDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @since 1.0.0
     */
    @Operation(summary = "Save game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameDTO> save(GameDTO dto) {
        GameDTO episode = service.save(dto);
        HttpHeaders headers = responseUtils.getHeaders(episode.getLanguage());

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(episode);
    }

    /**
     * Method that crates a game translation
     *
     * @param dto An {@link GameDTO} with all game fields
     * @return An {@link GameTranslationDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist or uuid is invalid
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Patch game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameTranslationDTO> saveTranslation(UUID uuid, GameTranslationDTO dto) {
        GameTranslationDTO translation = service.saveTranslation(uuid, dto);
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(translation);
    }

    /**
     * Method that updates a game
     *
     * @param uuid  An {@link UUID} that represents a specific item
     * @param patch An {@link GameDTO} with updated game fields
     * @return An {@link GameDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Patch game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameDTO> patch(UUID uuid, GameDTO patch) throws IOException {
        // All translation models will be queried with 'eager' type.
        // If 'language' is not provided, query will return more than one result, resulting in an error.
        patch.setLanguage(responseUtils.getDefaultLanguage());

        service.patch(uuid, patch);
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that updates a game translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @param patch    An {@link GameTranslationDTO} with updated game fields
     * @return An {@link GameTranslationDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @throws SaveConflictException If uuid is invalid
     * @since 1.0.0
     */
    @Operation(summary = "Patch game translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<GameTranslationDTO> patchTranslation(
            UUID uuid,
            String language,
            GameTranslationDTO patch
    ) throws IOException {
        service.patchTranslation(uuid, language, patch);
        HttpHeaders headers = responseUtils.getHeaders(language);

        return ResponseEntity.ok().headers(headers).body(patch);
    }

    /**
     * Method that delete a game and all translations
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete game", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Method that delete a game translation
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete game translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteTranslation(UUID uuid, String language) {
        service.deleteTranslation(uuid, language);
        return ResponseEntity.noContent().build();
    }
}
