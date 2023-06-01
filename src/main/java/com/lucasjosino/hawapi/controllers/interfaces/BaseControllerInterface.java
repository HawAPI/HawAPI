package com.lucasjosino.hawapi.controllers.interfaces;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BaseControllerInterface<D extends BaseDTO> {

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<List<D>> findAll(
            @Parameter(hidden = true) @RequestParam Map<String, String> filters,
            @Parameter(hidden = true) Pageable pageable
    );

    @GetMapping(value = "/random", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<D> findRandom(@RequestParam(required = false) String language);

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<D> findBy(@PathVariable UUID uuid, @RequestParam(required = false) String language);

    @PostMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<D> save(@Valid @RequestBody D dto);

    @PatchMapping(
            value = "/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<D> patch(@PathVariable UUID uuid, @Valid @RequestBody D dto);

    @DeleteMapping(value = "/{uuid}")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<Void> delete(@PathVariable UUID uuid);
}
