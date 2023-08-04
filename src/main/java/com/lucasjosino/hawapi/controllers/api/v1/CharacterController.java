package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseControllerInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.services.impl.CharacterServiceImpl;
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

/**
 * Endpoints for managing characters
 *
 * @author Lucas Josino
 * @see <a href="https://hawapi.theproject.id/docs/api/characters">HawAPI#Characters</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/characters")
@Tag(
        name = "Characters",
        description = "Endpoints for managing characters",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/characters"
        )
)
public class CharacterController implements BaseControllerInterface<CharacterDTO> {

    private final CharacterServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public CharacterController(CharacterServiceImpl service, ResponseUtils responseUtils) {
        this.service = service;
        this.responseUtils = responseUtils;
    }

    /**
     * Method that get all characters
     *
     * @param filters  An {@link Map} of params that represents {@link CharacterFilter}. Can be empty
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link List} of {@link CharacterDTO} or empty
     * @throws InternalServerErrorException If a valid filter is not provided
     * @since 1.0.0
     */
    @Operation(summary = "Get all characters")
    public ResponseEntity<List<CharacterDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        pageable = responseUtils.validateSort(pageable);

        Page<UUID> uuids = service.findAllUUIDs(filters, pageable);
        List<CharacterDTO> res = service.findAll(filters, uuids);
        HttpHeaders headers = responseUtils.getHeaders(uuids, null);

        return ResponseEntity.ok().headers(headers).body(res);
    }

    /**
     * Method that get a single random character
     *
     * @return An single {@link CharacterDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random character")
    public ResponseEntity<CharacterDTO> findRandom(String language) {
        return ResponseEntity.ok().body(service.findRandom(language));
    }

    /**
     * Method that get a single character
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An single {@link CharacterDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get character")
    public ResponseEntity<CharacterDTO> findBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findBy(uuid, language));
    }

    /**
     * Method that crates a character
     *
     * @param dto An {@link CharacterDTO} with all character fields
     * @return An {@link CharacterDTO} with the saved object
     * @throws BadRequestException If dto validation fail
     * @since 1.0.0
     */
    @Operation(summary = "Save character", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<CharacterDTO> save(CharacterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    /**
     * Method that updates a character
     *
     * @param uuid  An {@link UUID} that represents a specific item
     * @param patch An {@link CharacterDTO} with updated character fields
     * @return An {@link CharacterDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Patch character", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<CharacterDTO> patch(UUID uuid, CharacterDTO patch) throws IOException {
        service.patch(uuid, patch);
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that delete a character
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete character", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
