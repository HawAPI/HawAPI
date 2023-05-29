package com.lucasjosino.hawapi.interfaces;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.models.base.BaseTranslation;
import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface BaseTranslationInterface<M extends BaseDTO, T extends BaseTranslationDTO> extends BaseControllerInterface<M> {

    @GetMapping(value = "/{uuid}/translations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<List<T>> findAllTranslationsBy(@PathVariable UUID uuid);

    @GetMapping(value = "/{uuid}/translations/random", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<T> findRandomTranslation(@PathVariable UUID uuid);

    @GetMapping(value = "/{uuid}/translations/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<T> findTranslationBy(@PathVariable UUID uuid, @PathVariable String language);

    @PostMapping(
            value = "/{uuid}/translations",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<T> saveTranslation(@PathVariable UUID uuid, @RequestBody T dto);

    @PatchMapping(
            value = "/{uuid}/translations/{language}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<T> patchTranslation(@PathVariable UUID uuid, @PathVariable String language, @RequestBody T dto);

    @DeleteMapping(value = "/{uuid}/translations/{language}")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<Void> deleteTranslation(@PathVariable UUID uuid, @PathVariable String language);
}
