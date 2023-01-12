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

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertAuthEquals;
import static com.lucasjosino.hawapi.utils.TestsData.getNewUser;
import static com.lucasjosino.hawapi.utils.TestsData.getNewUserAuth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class AuthControllerUnitTest {

    private static final UserModel newUser = getNewUser();

    private static final UserAuthenticationModel newUserAuth = getNewUserAuth();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    /**
     * Auth registration will be false by default. So, we enable for tests.
     * <br>
     * <br>
     * See: <strong>/test/java/resources/application-test.properties</strong>
     */
    @BeforeEach
    public void setup() {
        authController.setRegistrationIsEnable(true);
    }

    @Test
    public void shouldRegisterUser() {
        when(authService.register(any(UserModel.class))).thenReturn(newUser);

        ResponseEntity<UserModel> res = authController.register(newUser);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals(newUser, res.getBody());
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldReturnConflictRegisterUser() {
        doThrow(UserConflictException.class).when(authService).register(any(UserModel.class));

        Exception exception = assertThrows(UserConflictException.class, () -> authController.register(newUser));
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldReturnUnauthorizedRegisterUserWithAdminRole() {
        newUser.setRole("ADMIN");
        doThrow(UserUnauthorizedException.class).when(authService).register(any(UserModel.class));

        Exception exception = assertThrows(UserUnauthorizedException.class, () -> authController.register(newUser));
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldReturnBadRequestRegisterUserWithUnknownRole() {
        newUser.setRole("UNKNOWN");
        doThrow(RoleBadRequestException.class).when(authService).register(any(UserModel.class));

        Exception exception = assertThrows(RoleBadRequestException.class, () -> authController.register(newUser));
        verify(authService, times(1)).register(any(UserModel.class));
    }

    @Test
    public void shouldAuthenticateUser() {
        when(authService.authenticate(any(UserAuthenticationModel.class))).thenReturn(newUser);

        ResponseEntity<UserModel> res = authController.authenticate(newUserAuth);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertAuthEquals(newUser, res);
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldAuthenticateUserWithoutPasswordButUsingAdminRole() {
        newUserAuth.setPassword(null);

        when(authService.authenticate(any(UserAuthenticationModel.class))).thenReturn(newUser);

        ResponseEntity<UserModel> res = authController.authenticate(newUserAuth);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertAuthEquals(newUser, res);
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnNotFoundAuthenticateUser() {
        doThrow(UserNotFoundException.class).when(authService).authenticate(any(UserAuthenticationModel.class));

        assertThrows(UserNotFoundException.class, () -> authController.authenticate(newUserAuth));
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnUnauthorizedAuthenticateUser() {
        newUserAuth.setPassword(null);

        doThrow(UserUnauthorizedException.class).when(authService).authenticate(any(UserAuthenticationModel.class));

        assertThrows(UserUnauthorizedException.class, () -> authController.authenticate(newUserAuth));
        verify(authService, times(1)).authenticate(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldDeleteUser() {
        doNothing().when(authService).delete(any(UserAuthenticationModel.class));

        ResponseEntity<Void> res = authController.delete(newUserAuth);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteUser() {
        doThrow(UserNotFoundException.class).when(authService).delete(any(UserAuthenticationModel.class));

        Exception exception = assertThrows(UserNotFoundException.class, () -> authController.delete(newUserAuth));
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }

    @Test
    public void shouldReturnUnauthorizedDeleteUser() {
        newUserAuth.setPassword(null);

        doThrow(UserUnauthorizedException.class).when(authService).delete(any(UserAuthenticationModel.class));

        Exception exception = assertThrows(UserUnauthorizedException.class, () -> authController.delete(newUserAuth));
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldDeleteUserWithoutPasswordButUsingAdminRole() {
        newUserAuth.setPassword(null);

        doNothing().when(authService).delete(any(UserAuthenticationModel.class));

        ResponseEntity<Void> res = authController.delete(newUserAuth);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(authService, times(1)).delete(any(UserAuthenticationModel.class));
    }
}
