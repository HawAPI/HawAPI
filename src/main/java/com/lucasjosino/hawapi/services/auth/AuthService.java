package com.lucasjosino.hawapi.services.auth;

import com.lucasjosino.hawapi.enums.auth.RoleType;
import com.lucasjosino.hawapi.exceptions.auth.RoleBadRequestException;
import com.lucasjosino.hawapi.exceptions.auth.UserConflictException;
import com.lucasjosino.hawapi.exceptions.auth.UserNotFoundException;
import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.jwt.JwtManager;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.repositories.auth.AuthRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtManager jwtManager;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtManager jwtManager) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtManager = jwtManager;
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

        String token = jwtManager.generateToken(user);

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
        UserModel user = validateUser(userAuth);

        String token = jwtManager.generateToken(user);

        return new UserModel() {{
            setNickname(user.getNickname());
            setEmail(user.getEmail());
            setRole(user.getRole());
            setToken(token);
            setCreatedAt(user.getCreatedAt());
            setUpdatedAt(user.getUpdatedAt());
        }};
    }

    @Transactional
    public void delete(UserAuthenticationModel userAuth) {
        UserModel dbUser = validateUser(userAuth);

        try {
            validatePassword(userAuth.getPassword(), dbUser.getPassword());
        } catch (UserUnauthorizedException userUnauthorized) {
            if (!hasAdminAuthorization()) {
                throw userUnauthorized;
            }
        }

        assert dbUser != null;
        authRepository.deleteById(dbUser.getUuid());
    }

    private UserModel validateUser(UserAuthenticationModel userAuth) {
        return authRepository.findByNicknameAndEmail(userAuth.getNickname(), userAuth.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found!")
                );
    }

    private void validatePassword(String userAuth, String dbUser) {
        if (!passwordEncoder.matches(userAuth, dbUser)) {
            throw new UserUnauthorizedException();
        }
    }

    private boolean hasAdminAuthorization() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(JwtManager.ROLE_PREFIX + RoleType.ADMIN)) return true;
        }

        return false;
    }
}
