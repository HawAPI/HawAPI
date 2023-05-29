package com.lucasjosino.hawapi.controllers.api.v1.auth;

import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.services.auth.AuthService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Auth",
        description = "Endpoints for managing auth",
        externalDocs = @ExternalDocumentation(
                url = "/docs/guides/authentication"
        )
)
public class AuthController {

    private final AuthService authService;

    @Value("${com.lucasjosino.hawapi.registration.enable}")
    private boolean registrationIsEnable;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            value = "/register",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Register user")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<UserDTO> register(@RequestBody UserModel user) {
        if (registrationIsEnable) {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(user));
        }

        throw new UserUnauthorizedException("Registration is not available at the moment");
    }

    @PostMapping(
            value = "/authenticate",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Authenticate user")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<UserDTO> authenticate(@RequestBody UserAuthenticationModel userAuth) {
        return ResponseEntity.ok(authService.authenticate(userAuth));
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<Void> delete(@RequestBody UserAuthenticationModel userAuth) {
        authService.delete(userAuth);
        return ResponseEntity.noContent().build();
    }

    public boolean isRegistrationIsEnable() {
        return registrationIsEnable;
    }

    public void setRegistrationIsEnable(boolean registrationIsEnable) {
        this.registrationIsEnable = registrationIsEnable;
    }
}
