package com.lucasjosino.hawapi.services.auth;

import com.lucasjosino.hawapi.enums.auth.RoleType;
import com.lucasjosino.hawapi.exceptions.auth.RoleBadRequestException;
import com.lucasjosino.hawapi.exceptions.auth.UserConflictException;
import com.lucasjosino.hawapi.exceptions.auth.UserNotFoundException;
import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.repositories.auth.AuthRepository;
import com.lucasjosino.hawapi.utils.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    public AuthService(
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public UserModel register(UserModel user) {
        if (authRepository.existsByNickname(user.getNickname())) {
            throw new UserConflictException("Nickname '" + user.getNickname() + "' already registered!");
        }

        if (authRepository.existsByEmail(user.getEmail())) {
            throw new UserConflictException("Email '" + user.getEmail() + "' already registered!");
        }

        if (!RoleType.isValid(user.getRole())) {
            throw new RoleBadRequestException("Role '" + user.getRole() + "' is not valid!");
        }

        // Create and set user uuid.
        UUID userUuid = UUID.randomUUID();
        user.setUuid(userUuid);

        String token = jwtUtils.generateToken(user);

        // Encode password.
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        authRepository.save(user);

        // Return a new user with basic information + token.
        return new UserModel() {{
            setNickname(user.getNickname());
            setRole(user.getRole());
            setToken(token);
            setTokenType("Bearer");
        }};
    }

    @Transactional
    public UserModel authenticate(UserAuthenticationModel userAuth) {
        UserModel user = authRepository.findByNicknameAndEmail(userAuth.getNickname(), userAuth.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found!")
                );

        if (!passwordEncoder.matches(userAuth.getPassword(), user.getPassword())) {
            throw new UserUnauthorizedException();
        }

        String token = jwtUtils.generateToken(user);

        return new UserModel() {{
            setNickname(user.getNickname());
            setEmail(user.getEmail());
            setRole(user.getRole());
            setToken(token);
            setCreatedAt(user.getCreatedAt());
            setUpdatedAt(user.getUpdatedAt());
        }};
    }
}
