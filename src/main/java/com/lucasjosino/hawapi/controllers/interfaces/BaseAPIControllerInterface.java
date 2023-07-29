package com.lucasjosino.hawapi.controllers.interfaces;

import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * A base API controller interface with all required endpoints.
 */
public interface BaseAPIControllerInterface {

    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping(value = "/endpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<String>> getEndpoints();

    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<OverviewDTO> getOverview(@RequestParam(required = false) String language);
}
