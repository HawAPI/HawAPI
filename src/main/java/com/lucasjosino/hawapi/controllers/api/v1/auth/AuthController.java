package com.lucasjosino.hawapi.controllers.api.v1.auth;

import com.lucasjosino.hawapi.controllers.interfaces.BaseAuthControllerInterface;
import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.models.dto.auth.UserAuthDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserRegistrationDTO;
import com.lucasjosino.hawapi.services.impl.auth.AuthServiceImpl;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class AuthController implements BaseAuthControllerInterface {

    private final AuthServiceImpl service;

    @Value("${com.lucasjosino.hawapi.registration.enable}")
    private boolean registrationIsEnable;

    public AuthController(AuthServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "Register user")
    public ResponseEntity<UserDTO> register(UserRegistrationDTO user) {
        if (registrationIsEnable) {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.register(user));
        }

        throw new UserUnauthorizedException("Registration is not available at the moment");
    }

    @Operation(summary = "Authenticate user")
    public ResponseEntity<UserDTO> authenticate(UserAuthDTO userAuth) {
        return ResponseEntity.ok(service.authenticate(userAuth));
    }

    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(UserAuthDTO userAuth) {
        service.delete(userAuth);
        return ResponseEntity.noContent().build();
    }

    public boolean isRegistrationIsEnable() {
        return registrationIsEnable;
    }

    public void setRegistrationIsEnable(boolean registrationIsEnable) {
        this.registrationIsEnable = registrationIsEnable;
    }
}
