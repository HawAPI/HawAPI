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
import java.util.Optional;
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

        if (user.getRole() != null) {
            if (!RoleType.isValid(user.getRole())) {
                throw new RoleBadRequestException("Role '" + user.getRole() + "' is not valid!");
            }

            // Only 'ADMIN' can create users with 'ADMIN' role.
            if (user.getRole().equals(RoleType.ADMIN.name())) {
                if (!hasAdminAuthorization()) {
                    throw new UserUnauthorizedException("Only user with ADMIN role can create ADMIN users");
                }
            }
        } else {
            user.setRole(RoleType.BASIC.name());
        }

        // Create and set user uuid.
        UUID userUuid = UUID.randomUUID();
        user.setUuid(userUuid);

        String token = jwtManager.generateToken(user);

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
        UserModel dbUser = validateUser(userAuth);

        try {
            validatePassword(userAuth.getPassword(), dbUser.getPassword());
        } catch (UserUnauthorizedException userUnauthorized) {
            if (!hasAdminAuthorization()) {
                throw userUnauthorized;
            }
        }

        String token = jwtManager.generateToken(dbUser);

        return new UserModel() {{
            setNickname(dbUser.getNickname());
            setEmail(dbUser.getEmail());
            setRole(dbUser.getRole());
            setToken(token);
            setCreatedAt(dbUser.getCreatedAt());
            setUpdatedAt(dbUser.getUpdatedAt());
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

    public String getRole() {
        Optional<? extends GrantedAuthority> firstRole = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream().findFirst();

        System.out.println("getRole");
        if (firstRole.isPresent()) {
            System.out.println("Role: " + firstRole.get());
            return firstRole.get().getAuthority();
        }

        System.out.println("Role: Nop");
        return JwtManager.ROLE_PREFIX + RoleType.ANONYMOUS.name();
    }

    private UserModel validateUser(UserAuthenticationModel userAuth) {
        return authRepository.findByNicknameAndEmail(userAuth.getNickname(), userAuth.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found!")
                );
    }

    private void validatePassword(String userAuth, String dbUser) {
        if (userAuth != null && !userAuth.isEmpty() && dbUser != null && !dbUser.isEmpty()) {
            if (passwordEncoder.matches(userAuth, dbUser)) return;
        }

        throw new UserUnauthorizedException();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
