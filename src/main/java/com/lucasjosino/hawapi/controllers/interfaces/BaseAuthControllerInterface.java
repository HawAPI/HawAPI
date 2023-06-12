package com.lucasjosino.hawapi.controllers.interfaces;

import com.lucasjosino.hawapi.models.dto.auth.UserAuthDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserRegistrationDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface BaseAuthControllerInterface {

    @PostMapping(
            value = "/register",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<UserDTO> register(@Valid @RequestBody UserRegistrationDTO user);

    @PostMapping(
            value = "/authenticate",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<UserDTO> authenticate(@Valid @RequestBody UserAuthDTO userAuth);

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    ResponseEntity<Void> delete(@Valid @RequestBody UserAuthDTO userAuth);
}
