package com.lucasjosino.hawapi.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.controllers.api.v1.auth.AuthController;
import com.lucasjosino.hawapi.models.dto.auth.UserAuthDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserRegistrationDTO;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.repositories.auth.AuthRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTestConfig
public class AuthIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/auth";

    private static final ModelMapper mapper = new ModelMapper();

    private UserDTO userDTO;

    @Autowired
    private AuthRepository repository;

    @Autowired
    private AuthController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("john_doe");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setRole("BASIC");
        userDTO.setEmail("johndoe@loremipsum.com");
        userDTO.setCreatedAt(LocalDateTime.now());
        userDTO.setUpdatedAt(LocalDateTime.now());
        userDTO.setToken("JWT");
        userDTO.setTokenType("Bearer");

        UserModel userModel = mapper.map(userDTO, UserModel.class);
        userModel.setUuid(UUID.randomUUID());
        userModel.setPassword(passwordEncoder.encode("MY_SUPER_SECRET_PASSWORD"));
        repository.save(userModel);
    }

    @AfterEach
    void tearDown() {
        deleteRepository();
    }

    private void deleteRepository() {
        repository.deleteAll();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        deleteRepository();

        UserRegistrationDTO registration = mapper.map(userDTO, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECRET_PASSWORD");

        mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.role").value(userDTO.getRole()))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    void whenRegistrationIsDisableShouldThrowUserUnauthorizedExceptionOnRegisterUser() throws Exception {
        UserRegistrationDTO registration = mapper.map(userDTO, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECRET_PASSWORD");

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
        UserRegistrationDTO registration = mapper.map(userDTO, UserRegistrationDTO.class);

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
        UserRegistrationDTO registration = mapper.map(userDTO, UserRegistrationDTO.class);
        registration.setPassword("MY_SUPER_SECRET_PASSWORD");
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
        UserAuthDTO authentication = mapper.map(userDTO, UserAuthDTO.class);
        authentication.setPassword("MY_SUPER_SECRET_PASSWORD");

        mockMvc.perform(post(URL + "/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authentication))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$.role").value(userDTO.getRole()))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").value(userDTO.getTokenType()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    void whenFieldValidationFailsShouldThrowBadRequestExceptionOnAuthenticateUser() throws Exception {
        UserAuthDTO authentication = mapper.map(userDTO, UserAuthDTO.class);

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
        UserAuthDTO deletion = mapper.map(userDTO, UserAuthDTO.class);
        deletion.setPassword("MY_SUPER_SECRET_PASSWORD");

        mockMvc.perform(post(URL + "/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletion))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenFieldValidationFailsShouldThrowBadRequestExceptionOnDeleteUser() throws Exception {
        UserAuthDTO deletion = mapper.map(userDTO, UserAuthDTO.class);

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
