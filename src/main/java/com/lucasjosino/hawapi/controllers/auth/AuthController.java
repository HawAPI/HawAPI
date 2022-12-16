package com.lucasjosino.hawapi.controllers.auth;

import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.services.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${hawapi.apiPath}/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> register(@RequestBody UserModel user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserModel> authenticate(@RequestBody UserAuthenticationModel userAuth) {
        return ResponseEntity.ok(authService.authenticate(userAuth));
    }

    @PostMapping("/update")
    public ResponseEntity<UserModel> update(@RequestBody UserModel user) {
        return ResponseEntity.ok(user);
    }

    @PostMapping("/delete")
    public ResponseEntity<UserModel> delete(@RequestBody UserModel user) {
        return ResponseEntity.ok(user);
    }
}
