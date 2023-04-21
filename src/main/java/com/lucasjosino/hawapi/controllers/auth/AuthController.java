package com.lucasjosino.hawapi.controllers.auth;

import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.services.auth.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${hawapi.apiPath}/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${com.lucasjosino.hawapi.registration.enable}")
    private boolean registrationIsEnable;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserModel user) {
        if (registrationIsEnable) {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(user));
        }

        throw new UserUnauthorizedException("Registration is not available at the moment");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserDTO> authenticate(@RequestBody UserAuthenticationModel userAuth) {
        return ResponseEntity.ok(authService.authenticate(userAuth));
    }

    @PostMapping("/delete")
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
