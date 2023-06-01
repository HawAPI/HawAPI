package com.lucasjosino.hawapi.services.impl.auth;

import com.lucasjosino.hawapi.enums.auth.RoleType;
import com.lucasjosino.hawapi.exceptions.auth.RoleBadRequestException;
import com.lucasjosino.hawapi.exceptions.auth.UserConflictException;
import com.lucasjosino.hawapi.exceptions.auth.UserNotFoundException;
import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.jwt.JwtManager;
import com.lucasjosino.hawapi.models.dto.auth.UserAuthDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserRegistrationDTO;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.repositories.auth.AuthRepository;
import com.lucasjosino.hawapi.services.auth.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtManager jwtManager;

    private final ModelMapper modelMapper;

    public AuthServiceImpl(
            AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtManager jwtManager,
            ModelMapper modelMapper
    ) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtManager = jwtManager;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public UserDTO register(UserRegistrationDTO user) {
        if (authRepository.existsByUsername(user.getUsername())) {
            throw new UserConflictException("Username '" + user.getUsername() + "' already registered!");
        }

        if (authRepository.existsByEmail(user.getEmail())) {
            throw new UserConflictException("Email '" + user.getEmail() + "' already registered!");
        }

        if (user.getRole() != null) {
            if (!RoleType.isValid(user.getRole())) {
                throw new RoleBadRequestException("Role '" + user.getRole() + "' is not valid!");
            }

            boolean isDevOrAdmin = user.getRole().equalsIgnoreCase(RoleType.DEV.name()) ||
                    user.getRole().equalsIgnoreCase(RoleType.ADMIN.name());

            if (isDevOrAdmin && !hasAdminAuthorization()) {
                throw new UserUnauthorizedException(
                        "Only user with ADMIN role can create users with role:'" + user.getRole() + "'"
                );
            }
        } else {
            user.setRole(RoleType.BASIC.name());
        }

        UserModel userModel = modelMapper.map(user, UserModel.class);

        // Create and set user uuid.
        UUID userUuid = UUID.randomUUID();
        userModel.setUuid(userUuid);

        String token = jwtManager.generateToken(userModel);

        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userModel.setRole(userModel.getRole().toUpperCase());

        authRepository.save(userModel);

        // Return a new user with basic information + token.
        return new UserDTO() {{
            setUsername(userModel.getUsername());
            setRole(userModel.getRole());
            setToken(token);
            setTokenType("Bearer");
        }};
    }

    @Transactional
    public UserDTO authenticate(UserAuthDTO userAuth) {
        UserModel dbUser = validateUser(userAuth);

        try {
            validatePassword(userAuth.getPassword(), dbUser.getPassword());
        } catch (UserUnauthorizedException userUnauthorized) {
            if (!hasAdminAuthorization()) {
                throw userUnauthorized;
            }
        }

        String token = jwtManager.generateToken(dbUser);

        return new UserDTO() {{
            setUsername(dbUser.getUsername());
            setEmail(dbUser.getEmail());
            setRole(dbUser.getRole());
            setToken(token);
            setCreatedAt(dbUser.getCreatedAt());
            setUpdatedAt(dbUser.getUpdatedAt());
        }};
    }

    @Transactional
    public void delete(UserAuthDTO userAuth) {
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
        Optional<? extends GrantedAuthority> firstAuthority = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst();

        if (firstAuthority.isPresent()) return firstAuthority.get().getAuthority();

        return JwtManager.ROLE_PREFIX + RoleType.ANONYMOUS.name();
    }

    private UserModel validateUser(UserAuthDTO userAuth) {
        return authRepository.findByUsernameAndEmail(userAuth.getUsername(), userAuth.getEmail())
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