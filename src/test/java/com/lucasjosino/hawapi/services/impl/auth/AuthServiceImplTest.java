package com.lucasjosino.hawapi.services.impl.auth;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ContextConfiguration()
@ExtendWith(SpringExtension.class)
class AuthServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private UserDTO user;

    @InjectMocks
    private AuthServiceImpl service;

    @Mock
    private AuthRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtManager jwtManager;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        user = new UserDTO();
        user.setUsername("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("BASIC");
        user.setEmail("johndoe@loremipsum.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setToken("JWT");
        user.setTokenType("Bearer");
    }

    @Test
    void shouldRegisterUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(any(), any())).thenReturn(userModel);
        when(jwtManager.generateToken(any(UserModel.class))).thenReturn("TOKEN");
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        when(repository.save(userModel)).thenReturn(userModel);

        UserDTO res = service.register(registration);

        assertEquals(userModel.getUsername(), res.getUsername());
        assertEquals(userModel.getRole(), res.getRole());
        assertEquals(userModel.getTokenType(), "Bearer");
        assertNotNull(res.getToken());
        assertFalse(res.getToken().isEmpty());

        verify(repository, times(1)).existsByUsername(anyString());
        verify(repository, times(1)).existsByEmail(anyString());
        verify(modelMapper, times(1)).map(any(), any());
        verify(jwtManager, times(1)).generateToken(any(UserModel.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(repository, times(1)).save(userModel);
    }

    @Test
    void shouldRegisterUserWithNotRoleDefined() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setRole(null);
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(any(), any())).thenReturn(userModel);
        when(jwtManager.generateToken(any(UserModel.class))).thenReturn("TOKEN");
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        when(repository.save(userModel)).thenReturn(userModel);

        UserDTO res = service.register(registration);

        assertEquals(userModel.getUsername(), res.getUsername());
        assertEquals(userModel.getRole(), res.getRole());
        assertEquals(userModel.getTokenType(), "Bearer");
        assertNotNull(res.getToken());
        assertFalse(res.getToken().isEmpty());

        verify(repository, times(1)).existsByUsername(anyString());
        verify(repository, times(1)).existsByEmail(anyString());
        verify(modelMapper, times(1)).map(any(), any());
        verify(jwtManager, times(1)).generateToken(any(UserModel.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(repository, times(1)).save(userModel);
    }

    @Test
    void whenInvalidRoleIsProvidedShouldThrowRoleBadRequestExceptionOnRegisterUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setRole("ANY_OTHER_ROLE");
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(RoleBadRequestException.class, () -> service.register(registration));

        verify(repository, times(1)).existsByUsername(anyString());
        verify(repository, times(1)).existsByEmail(anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldRegisterUserWithAdminRole() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setRole("ADMIN");
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(any(), any())).thenReturn(userModel);
        when(jwtManager.generateToken(any(UserModel.class))).thenReturn("TOKEN");
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        when(repository.save(userModel)).thenReturn(userModel);

        UserDTO res = service.register(registration);

        assertEquals(userModel.getUsername(), res.getUsername());
        assertEquals(userModel.getRole(), res.getRole());
        assertEquals(userModel.getTokenType(), "Bearer");
        assertNotNull(res.getToken());
        assertFalse(res.getToken().isEmpty());

        verify(repository, times(1)).existsByUsername(anyString());
        verify(repository, times(1)).existsByEmail(anyString());
        verify(modelMapper, times(1)).map(any(), any());
        verify(jwtManager, times(1)).generateToken(any(UserModel.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(repository, times(1)).save(userModel);
    }

    @Test
    @WithMockUser(username = "anonymous", roles = "ANONYMOUS")
    void whenAdminRoleIsProvidedWithoutAuthenticationShouldThrowUserUnauthorizedExceptionOnRegisterUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setRole("ADMIN");
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(UserUnauthorizedException.class, () -> service.register(registration));

        verify(repository, times(1)).existsByUsername(anyString());
        verify(repository, times(1)).existsByEmail(anyString());
    }

    @Test
    @WithMockUser(username = "basic", roles = "BASIC")
    void whenDevRoleIsProvidedWithWrongAuthenticationShouldThrowUserUnauthorizedExceptionOnRegisterUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setRole("DEV");
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(UserUnauthorizedException.class, () -> service.register(registration));

        verify(repository, times(1)).existsByUsername(anyString());
        verify(repository, times(1)).existsByEmail(anyString());
    }

    @Test
    void whenUserExistsByUsernameShouldThrowUserConflictExceptionOnRegisterUser() {
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UserConflictException.class, () -> service.register(registration));

        verify(repository, times(1)).existsByUsername(anyString());
    }

    @Test
    void whenUserExistsByEmailShouldThrowUserConflictExceptionOnRegisterUser() {
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserConflictException.class, () -> service.register(registration));

        verify(repository, times(1)).existsByUsername(anyString());
        verify(repository, times(1)).existsByEmail(anyString());
    }

    @Test
    void shouldAuthenticateUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");
        authentication.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.of(userModel));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtManager.generateToken(any(UserModel.class))).thenReturn("TOKEN");

        UserDTO res = service.authenticate(authentication);

        assertEquals(userModel.getUsername(), res.getUsername());
        assertEquals(userModel.getEmail(), res.getEmail());
        assertEquals(userModel.getRole(), res.getRole());
        assertEquals(userModel.getUsername(), res.getUsername());
        assertNotNull(res.getToken());
        assertNotNull(res.getCreatedAt());
        assertNotNull(res.getUpdatedAt());
        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtManager, times(1)).generateToken(any(UserModel.class));
    }

    @Test
    void whenNoUserFoundShouldThrowUserNotFoundExceptionOnAuthenticateUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");
        authentication.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.authenticate(authentication));

        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAuthenticateUserWithAdminRole() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        authentication.setPassword("ANY_OTHER_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.of(userModel));

        UserDTO res = service.authenticate(authentication);

        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "anonymous", roles = "ANONYMOUS")
    void whenWrongPasswordIsProvidedWithoutAdminAuthenticationShouldThrowUserUnauthorizedExceptionOnAuthenticateUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");
        authentication.setPassword("ANY_OTHER_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.of(userModel));

        assertThrows(UserUnauthorizedException.class, () -> service.authenticate(authentication));

        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "anonymous", roles = "ANONYMOUS")
    void shouldDeleteUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        userModel.setUuid(UUID.randomUUID());
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");
        authentication.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.of(userModel));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.delete(authentication);

        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoUserFoundShouldThrowUserNotFoundExceptionOnDeleteUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");
        authentication.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.delete(authentication));

        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteUserWithAdminRole() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        authentication.setPassword("ANY_OTHER_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.of(userModel));

        service.delete(authentication);

        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "anonymous", roles = "ANONYMOUS")
    void whenWrongPasswordIsProvidedWithoutAdminAuthenticationShouldThrowUserUnauthorizedExceptionOnDeleteUser() {
        UserModel userModel = mapper.map(user, UserModel.class);
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        userModel.setPassword("MY_SUPER_SECURE_PASSWORD");
        authentication.setPassword("ANY_OTHER_PASSWORD");

        when(repository.findByUsernameAndEmail(anyString(), anyString())).thenReturn(Optional.of(userModel));

        assertThrows(UserUnauthorizedException.class, () -> service.delete(authentication));

        verify(repository, times(1)).findByUsernameAndEmail(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "basic", roles = "BASIC")
    void shouldReturnCurrentUserRoleWithBasicAuthentication() {
        String res = service.getRole();

        assertEquals("ROLE_BASIC", res);
    }

    @Test
    @WithMockUser(username = "anonymous", roles = "ANONYMOUS")
    void shouldReturnCurrentUserRoleWithAnonymousAuthentication() {
        String res = service.getRole();

        assertEquals("ROLE_ANONYMOUS", res);
    }

    @Test
    @WithMockUser(username = "dev", roles = "DEV")
    void shouldReturnCurrentUserRoleWithDevAuthentication() {
        String res = service.getRole();

        assertEquals("ROLE_DEV", res);
    }
}