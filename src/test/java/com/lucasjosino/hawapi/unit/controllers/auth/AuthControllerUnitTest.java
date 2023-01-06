package com.lucasjosino.hawapi.unit.controllers.auth;

import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.auth.AuthController;
import com.lucasjosino.hawapi.exceptions.auth.RoleBadRequestException;
import com.lucasjosino.hawapi.exceptions.auth.UserConflictException;
import com.lucasjosino.hawapi.exceptions.auth.UserNotFoundException;
import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.services.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Objects;

import static com.lucasjosino.hawapi.utils.TestsData.getNewUser;
import static com.lucasjosino.hawapi.utils.TestsData.getNewUserAuth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// shouldRegisterUser
// shouldReturnConflictRegisterUser
// shouldReturnUnauthorizedRegisterUserWithAdminRole
// shouldReturnBadRequestRegisterUserWithUnknownRole
// shouldAuthenticateUser
// shouldAuthenticateUserWithoutPasswordButUsingAdminRole
// shouldReturnNotFoundAuthenticateUser
// shouldReturnUnauthorizedAuthenticateUser
// shouldDeleteUser
// shouldReturnNotFoundDeleteUser
// shouldReturnUnauthorizedDeleteUser
// shouldDeleteUserWithoutPasswordButUsingAdminRole

@UnitTestConfig
public class AuthControllerUnitTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        authController.setRegistrationIsEnable(true);
    }

    @Test
    public void shouldRegisterUser() {
        UserModel newUser = getNewUser();
        when(authService.register(any(UserModel.class))).thenReturn(newUser);

        ResponseEntity<UserModel> res = authController.register(newUser);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals(newUser, res.getBody());
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldReturnConflictRegisterUser() {
        UserModel newUser = getNewUser();
        doThrow(UserConflictException.class).when(authService).register(any(UserModel.class));

        Exception exception = assertThrows(UserConflictException.class, () -> authController.register(newUser));
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldReturnUnauthorizedRegisterUserWithAdminRole() {
        UserModel newUser = getNewUser();
        newUser.setRole("ADMIN");
        doThrow(UserUnauthorizedException.class).when(authService).register(any(UserModel.class));

        Exception exception = assertThrows(UserUnauthorizedException.class, () -> authController.register(newUser));
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldReturnBadRequestRegisterUserWithUnknownRole() {
        UserModel newUser = getNewUser();
        newUser.setRole("UNKNOWN");
        doThrow(RoleBadRequestException.class).when(authService).register(any(UserModel.class));

        Exception exception = assertThrows(RoleBadRequestException.class, () -> authController.register(newUser));
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldAuthenticateUser() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();

        when(authService.authenticate(any(UserAuthenticationModel.class))).thenReturn(user);

        ResponseEntity<UserModel> res = authController.authenticate(userAuth);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(user.getNickname(), Objects.requireNonNull(res.getBody()).getNickname());
        assertEquals(user.getEmail(), Objects.requireNonNull(res.getBody()).getEmail());
        assertEquals(user.getRole(), Objects.requireNonNull(res.getBody()).getRole());
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldAuthenticateUserWithoutPasswordButUsingAdminRole() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();
        userAuth.setPassword(null);

        when(authService.authenticate(any(UserAuthenticationModel.class))).thenReturn(user);

        ResponseEntity<UserModel> res = authController.authenticate(userAuth);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(user.getNickname(), Objects.requireNonNull(res.getBody()).getNickname());
        assertEquals(user.getEmail(), Objects.requireNonNull(res.getBody()).getEmail());
        assertEquals(user.getRole(), Objects.requireNonNull(res.getBody()).getRole());
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnNotFoundAuthenticateUser() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();

        doThrow(UserNotFoundException.class).when(authService).authenticate(any(UserAuthenticationModel.class));

        assertThrows(UserNotFoundException.class, () -> authController.authenticate(userAuth));
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnUnauthorizedAuthenticateUser() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();
        userAuth.setPassword(null);

        doThrow(UserUnauthorizedException.class).when(authService).authenticate(any(UserAuthenticationModel.class));

        assertThrows(UserUnauthorizedException.class, () -> authController.authenticate(userAuth));
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldDeleteUser() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();

        doNothing().when(authService).delete(any(UserAuthenticationModel.class));

        ResponseEntity<Void> res = authController.delete(userAuth);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteUser() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();

        doThrow(UserNotFoundException.class).when(authService).delete(any(UserAuthenticationModel.class));

        Exception exception = assertThrows(UserNotFoundException.class, () -> authController.delete(userAuth));
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnUnauthorizedDeleteUser() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();
        userAuth.setPassword(null);

        doThrow(UserUnauthorizedException.class).when(authService).delete(any(UserAuthenticationModel.class));

        Exception exception = assertThrows(UserUnauthorizedException.class, () -> authController.delete(userAuth));
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldDeleteUserWithoutPasswordButUsingAdminRole() {
        UserModel user = getNewUser();
        UserAuthenticationModel userAuth = getNewUserAuth();
        userAuth.setPassword(null);

        doNothing().when(authService).delete(any(UserAuthenticationModel.class));

        ResponseEntity<Void> res = authController.delete(userAuth);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }
}
