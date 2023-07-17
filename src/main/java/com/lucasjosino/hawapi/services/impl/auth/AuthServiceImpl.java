package com.lucasjosino.hawapi.services.impl.auth;

import com.lucasjosino.hawapi.controllers.api.v1.auth.AuthController;
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

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Methods to handle auth
 *
 * @author Lucas Josino
 * @see AuthController
 * @since 1.0.0
 */
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

    /**
     * Method that crates a user on the database
     *
     * @see AuthController#register(UserRegistrationDTO)
     * @since 1.0.0
     */
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

            if (user.getRole().equalsIgnoreCase(RoleType.ANONYMOUS.name())) {
                throw new RoleBadRequestException("Role 'ANONYMOUS' doesn't require registration");
            }

            boolean requireAdmin = user.getRole().equalsIgnoreCase(RoleType.DEV.name())
                    || user.getRole().equalsIgnoreCase(RoleType.MAINTAINER.name())
                    || user.getRole().equalsIgnoreCase(RoleType.ADMIN.name());

            if (requireAdmin && !hasAdminAuthorization()) {
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

    /**
     * Method that authenticate a user from the database
     *
     * @see AuthController#authenticate(UserAuthDTO)
     * @since 1.0.0
     */
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
            // TODO: Add token type
            setCreatedAt(dbUser.getCreatedAt());
            setUpdatedAt(dbUser.getUpdatedAt());
        }};
    }

    /**
     * Method that delete a user from the database
     *
     * @see AuthController#delete(UserAuthDTO)
     * @since 1.0.0
     */
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

    /**
     * Method to get current user role
     *
     * @return An {@link String} representing user role
     * @see RoleType
     * @since 1.0.0
     */
    public String getRole() {
        Optional<? extends GrantedAuthority> firstAuthority = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst();

        if (firstAuthority.isPresent()) return firstAuthority.get().getAuthority();

        return JwtManager.ROLE_PREFIX + RoleType.ANONYMOUS.name();
    }

    /**
     * Method to validate user using its <strong>username</strong> and <strong>email</strong>
     *
     * @return An {@link UserModel} representing user
     * @throws UserNotFoundException If no user was found
     * @see AuthRepository#existsByUsernameAndEmail(String, String)
     * @since 1.0.0
     */
    private UserModel validateUser(UserAuthDTO userAuth) {
        return authRepository.findByUsernameAndEmail(userAuth.getUsername(), userAuth.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found!")
                );
    }

    /**
     * Method to validate user password
     *
     * @throws UserUnauthorizedException If user is not authenticated/validated
     * @see PasswordEncoder
     * @since 1.0.0
     */
    private void validatePassword(String userAuth, String dbUser) {
        if (userAuth != null && !userAuth.isEmpty() && dbUser != null && !dbUser.isEmpty()) {
            if (passwordEncoder.matches(userAuth, dbUser)) return;
        }

        throw new UserUnauthorizedException();
    }

    /**
     * Method to validate if user is authenticated with <strong>ADMIN</strong> role
     *
     * @return true if user is an ADMIN
     * @throws UserUnauthorizedException If user is not authenticated/validated
     * @see GrantedAuthority
     * @see SecurityContextHolder
     * @see RoleType
     * @since 1.0.0
     */
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

    /**
     * Values for the role parameter.
     *
     * @author Lucas Josino
     * @see AuthServiceImpl
     * @since 1.0.0
     */
    public enum RoleType {
        ANONYMOUS,
        BASIC,
        DEV,
        MAINTAINER,
        ADMIN;

        /**
         * Validate if param is a role
         * <p> Before validation, the param will be converted to lowercase
         *
         * @param name An {@link String} to be validated
         * @return true if param is a valid role name
         * @since 1.0.0
         */
        public static boolean isValid(String name) {
            for (RoleType role : values()) {
                if (role.name().equalsIgnoreCase(name)) return true;
            }
            return false;
        }
    }
}