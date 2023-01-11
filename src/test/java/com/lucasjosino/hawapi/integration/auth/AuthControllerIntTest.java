package com.lucasjosino.hawapi.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.user.UserModel;
import com.lucasjosino.hawapi.repositories.auth.AuthRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.lucasjosino.hawapi.utils.TestsData.getAdminAuth;
import static com.lucasjosino.hawapi.utils.TestsData.getNewUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestConfig
public class AuthControllerIntTest extends DatabaseContainerInitializer {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        authRepository.deleteAll();
    }

    @AfterAll
    public void cleanUp() {
        authRepository.deleteAll();
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        UserModel user = getNewUser();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.role").value("DEV"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    public void shouldReturnConflictRegisterUser() throws Exception {
        UserModel user = getNewUser();

        authRepository.save(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnUnauthorizedRegisterUserWithAdminRole() throws Exception {
        UserModel user = getNewUser();
        user.setRole("ADMIN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnBadRequestRegisterUserWithUnknownRole() throws Exception {
        UserModel user = getNewUser();
        user.setRole("UNKNOWN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAuthenticateUser() throws Exception {
        UserModel user = getNewUser();
        saveTempUser(user);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value(user.getRole()))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldAuthenticateUserWithoutPasswordButUsingAdminRole() throws Exception {
        UserModel user = getNewUser();
        saveTempUser(user);

        user.setPassword(null);

        mockMvc.perform(post("/api/auth/authenticate")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value(user.getRole()))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundAuthenticateUser() throws Exception {
        UserModel user = getNewUser();

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedAuthenticateUser() throws Exception {
        UserModel user = getNewUser();
        saveTempUser(user);

        user.setPassword("SOMEWRONGPASSWORD");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        UserModel user = getNewUser();
        saveTempUser(user);

        mockMvc.perform(post("/api/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteUser() throws Exception {
        UserModel user = getNewUser();

        mockMvc.perform(post("/api/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteUser() throws Exception {
        UserModel user = getNewUser();
        saveTempUser(user);

        user.setPassword("SOMEWRONGPASSWORD");

        mockMvc.perform(post("/api/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldDeleteUserWithoutPasswordButUsingAdminRole() throws Exception {
        UserModel user = getNewUser();
        saveTempUser(user);

        user.setPassword(null);

        mockMvc.perform(post("/api/auth/delete")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private void saveTempUser(UserModel user) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
