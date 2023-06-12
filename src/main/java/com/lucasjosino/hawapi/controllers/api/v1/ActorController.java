package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseControllerInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.dto.ActorDTO;
import com.lucasjosino.hawapi.services.impl.ActorServiceImpl;
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

/**
 * Endpoints for managing actors
 *
 * @author Lucas Josino
 * @see <a href="https://hawapi.theproject.id/docs/api/actors">HawAPI#Characters</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/actors")
@Tag(
        name = "Actors",
        description = "Endpoints for managing actors",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/actors"
        )
)
public class ActorController implements BaseControllerInterface<ActorDTO> {

    private final ActorServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public ActorController(ActorServiceImpl service, ResponseUtils responseUtils) {
        this.service = service;
        this.responseUtils = responseUtils;
    }

    /**
     * Method that get all actors
     *
     * @param filters  An {@link Map} of params that represents {@link ActorFilter}. Can be empty
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link List} of {@link ActorDTO} or empty
     * @throws InternalServerErrorException If a valid filter is not provided
     * @since 1.0.0
     */
    @Operation(summary = "Get all actors")
    public ResponseEntity<List<ActorDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        Page<UUID> uuids = service.findAllUUIDs(pageable);
        HttpHeaders headers = responseUtils.getHeaders(
                uuids,
                pageable,
                null
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<ActorDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    /**
     * Method that get a single random actor
     *
     * @return An single {@link ActorDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get random actor")
    public ResponseEntity<ActorDTO> findRandom(String language) {
        return ResponseEntity.ok().body(service.findRandom(language));
    }

    /**
     * Method that get a single actor
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An single {@link ActorDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get actor")
    public ResponseEntity<ActorDTO> findBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findBy(uuid, language));
    }

    /**
     * Method that crates an actor
     *
     * @param dto An {@link ActorDTO} with all actor fields
     * @return An {@link ActorDTO} with the saved object
     * @throws BadRequestException If dto validation fail
     * @since 1.0.0
     */
    @Operation(summary = "Save actor", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<ActorDTO> save(ActorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    /**
     * Method that updates an actor
     *
     * @param uuid  An {@link UUID} that represents a specific item
     * @param patch An {@link ActorDTO} with updated actor fields
     * @return An {@link ActorDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Patch actor", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<ActorDTO> patch(UUID uuid, ActorDTO patch) {
        try {
            service.patch(uuid, patch);
        } catch (ItemNotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that delete an actor
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete actor", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
