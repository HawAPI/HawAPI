package com.lucasjosino.hawapi.controllers.api.v1.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.models.dto.auth.UserAuthDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserRegistrationDTO;
import com.lucasjosino.hawapi.services.impl.auth.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = {AuthController.class, ControllerAdvisor.class, SecurityConfig.class})
class AuthControllerTest {

    private static final String URL = "/api/v1/auth";

    private static final ModelMapper mapper = new ModelMapper();

    private UserDTO user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController controller;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthServiceImpl service;

    @MockBean
    private JwtDecoder jwtDecoder;

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
    void shouldRegisterUser() throws Exception {
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(service.register(any(UserRegistrationDTO.class))).thenReturn(user);

        mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.role").value(user.getRole()))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").value("Bearer"));

        verify(service, times(1)).register(any(UserRegistrationDTO.class));
    }

    @Test
    void whenRegistrationIsDisableShouldThrowUserUnauthorizedExceptionOnRegisterUser() throws Exception {
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");

        // Overwrite 'registrationIsEnable' value
        controller.setRegistrationIsEnable(false);

        mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Registration is not available at the moment"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/register"));
    }

    @Test
    void whenFieldValidationFailsShouldThrowBadRequestExceptionOnRegisterUser() throws Exception {
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);

        mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'password' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/register"));
    }

    @Test
    void whenRoleFieldValidationFailsShouldThrowBadRequestExceptionOnRegisterUser() throws Exception {
        UserRegistrationDTO registration = mapper.map(user, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECURE_PASSWORD");
        registration.setRole("SUPER_ADMIN");

        mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value(
                        "For the account type only the values BASIC, DEV, or ADMIN are accepted."))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/register"));
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);
        authentication.setPassword("MY_SUPER_SECURE_PASSWORD");

        when(service.authenticate(any(UserAuthDTO.class))).thenReturn(user);

        mockMvc.perform(post(URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authentication))
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).authenticate(any(UserAuthDTO.class));
    }

    @Test
    void whenFieldValidationFailsShouldThrowBadRequestExceptionOnAuthenticateUser() throws Exception {
        UserAuthDTO authentication = mapper.map(user, UserAuthDTO.class);

        mockMvc.perform(post(URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authentication))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'password' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/authenticate"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserAuthDTO deletion = mapper.map(user, UserAuthDTO.class);
        deletion.setPassword("MY_SUPER_SECURE_PASSWORD");

        doNothing().when(service).delete(any(UserAuthDTO.class));

        mockMvc.perform(post(URL + "/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletion))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(any(UserAuthDTO.class));
    }

    @Test
    void whenFieldValidationFailsShouldThrowBadRequestExceptionOnDeleteUser() throws Exception {
        UserAuthDTO deletion = mapper.map(user, UserAuthDTO.class);

        mockMvc.perform(post(URL + "/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletion))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'password' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/delete"));
    }
}