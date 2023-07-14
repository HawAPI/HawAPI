package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseControllerInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SoundtrackFilter;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.services.impl.SoundtrackServiceImpl;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints for managing soundtracks
 *
 * @author Lucas Josino
 * @see <a href="https://hawapi.theproject.id/docs/api/soundtracks">HawAPI#Characters</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/soundtracks")
@Tag(
        name = "Soundtracks",
        description = "Endpoints for managing soundtracks",
        externalDocs = @ExternalDocumentation(
                url = "/docs/api/soundtracks"
        )
)
public class SoundtrackController implements BaseControllerInterface<SoundtrackDTO> {

    private final SoundtrackServiceImpl service;

    private final ResponseUtils responseUtils;

    @Autowired
    public SoundtrackController(SoundtrackServiceImpl SoundtrackService, ResponseUtils responseUtils) {
        this.service = SoundtrackService;
        this.responseUtils = responseUtils;
    }

    /**
     * Method that get all soundtracks
     *
     * @param filters  An {@link Map} of params that represents {@link SoundtrackFilter}. Can be empty
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link List} of {@link SoundtrackDTO} or empty
     * @throws InternalServerErrorException If a valid filter is not provided
     * @since 1.0.0
     */
    @GetMapping
    public ResponseEntity<List<SoundtrackDTO>> findAll(Map<String, String> filters, Pageable pageable) {
        long count = service.getCount();
        Page<UUID> uuids = service.findAllUUIDs(pageable, count);
        HttpHeaders headers = responseUtils.getHeaders(
                uuids,
                pageable,
                null,
                count
        );

        if (uuids.isEmpty()) ResponseEntity.ok().headers(headers).body(Collections.emptyList());

        List<SoundtrackDTO> res = service.findAll(filters, uuids.getContent());
        return ResponseEntity.ok().headers(headers).body(res);
    }

    /**
     * Method that get a single random soundtrack
     *
     * @return An single {@link SoundtrackDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @GetMapping("/random")
    public ResponseEntity<SoundtrackDTO> findRandom(String language) {
        return ResponseEntity.ok().body(service.findRandom(language));
    }

    /**
     * Method that get a single soundtrack
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An single {@link SoundtrackDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<SoundtrackDTO> findBy(UUID uuid, String language) {
        return ResponseEntity.ok(service.findBy(uuid, language));
    }

    /**
     * Method that crates a soundtrack
     *
     * @param dto An {@link SoundtrackDTO} with all soundtrack fields
     * @return An {@link SoundtrackDTO} with the saved object
     * @throws BadRequestException If dto validation fail
     * @since 1.0.0
     */
    @PostMapping
    public ResponseEntity<SoundtrackDTO> save(SoundtrackDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    /**
     * Method that updates a soundtrack
     *
     * @param uuid  An {@link UUID} that represents a specific item
     * @param patch An {@link SoundtrackDTO} with updated soundtrack fields
     * @return An {@link SoundtrackDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @PatchMapping("/{uuid}")
    public ResponseEntity<SoundtrackDTO> patch(UUID uuid, SoundtrackDTO patch) throws IOException {
        service.patch(uuid, patch);
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that delete a soundtrack
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(UUID uuid) {
        service.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
