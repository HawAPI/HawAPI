package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.models.dto.ActorDTO;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTestConfig
class ActorIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/actors";

    private static final ModelMapper mapper = new ModelMapper();

    private ActorDTO actorDTO;

    @Autowired
    private ActorRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        actorDTO = new ActorDTO();
        actorDTO.setUuid(UUID.randomUUID());
        actorDTO.setHref(URL + "/" + actorDTO.getUuid());
        actorDTO.setFirstName("Lorem");
        actorDTO.setLastName("Ipsum");
        actorDTO.setNationality("American");
        actorDTO.setSeasons(Arrays.asList("/api/v1/seasons/1", "/api/v1/seasons/2"));
        actorDTO.setGender((byte) 1);
        actorDTO.setBirthDate(LocalDate.now());
        actorDTO.setCharacter("/api/v1/characters/1");
        actorDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        actorDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        actorDTO.setCreatedAt(LocalDateTime.now());
        actorDTO.setUpdatedAt(LocalDateTime.now());

        ActorModel characterModel = mapper.map(actorDTO, ActorModel.class);
        repository.save(characterModel);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllActors() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "10"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyList() throws Exception {
        repository.deleteAll();
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "10"))
                .andExpect(header().string("X-Pagination-Page-Total", "0"))
                .andExpect(header().string("X-Pagination-Item-Total", "0"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnRandomActor() throws Exception {
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(actorDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(actorDTO.getHref()))
                .andExpect(jsonPath("$.first_name").value(actorDTO.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actorDTO.getLastName()))
                .andExpect(jsonPath("$.nationality").value(actorDTO.getNationality()))
                .andExpect(jsonPath("$.seasons").isNotEmpty())
                .andExpect(jsonPath("$.gender").value(String.valueOf(actorDTO.getGender())))
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(actorDTO.getBirthDate())))
                .andExpect(jsonPath("$.character").value(actorDTO.getCharacter()))
                .andExpect(jsonPath("$.thumbnail").value(actorDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnRandomActor() throws Exception {
        repository.deleteAll();
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnActorByUUID() throws Exception {
        mockMvc.perform(get(URL + "/" + actorDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(actorDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(actorDTO.getHref()))
                .andExpect(jsonPath("$.first_name").value(actorDTO.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actorDTO.getLastName()))
                .andExpect(jsonPath("$.nationality").value(actorDTO.getNationality()))
                .andExpect(jsonPath("$.seasons").isNotEmpty())
                .andExpect(jsonPath("$.gender").value(String.valueOf(actorDTO.getGender())))
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(actorDTO.getBirthDate())))
                .andExpect(jsonPath("$.character").value(actorDTO.getCharacter()))
                .andExpect(jsonPath("$.thumbnail").value(actorDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnActorByUUID() throws Exception {
        repository.deleteAll();
        mockMvc.perform(get(URL + "/" + actorDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveActor() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actorDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.first_name").value(actorDTO.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actorDTO.getLastName()))
                .andExpect(jsonPath("$.nationality").value(actorDTO.getNationality()))
                .andExpect(jsonPath("$.seasons").isNotEmpty())
                .andExpect(jsonPath("$.gender").value(String.valueOf(actorDTO.getGender())))
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(actorDTO.getBirthDate())))
                .andExpect(jsonPath("$.character").value(actorDTO.getCharacter()))
                .andExpect(jsonPath("$.thumbnail").value(actorDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveActor() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actorDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveActor() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actorDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveActor() throws Exception {
        actorDTO.setFirstName(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actorDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'first_name' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void shouldUpdateActor() throws Exception {
        ActorDTO patch = new ActorDTO();
        patch.setGender((byte) 0);

        mockMvc.perform(patch(URL + "/" + actorDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.gender").value(String.valueOf(patch.getGender())));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateActor() throws Exception {
        mockMvc.perform(patch(URL + "/" + actorDTO.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateActor() throws Exception {
        mockMvc.perform(patch(URL + "/" + actorDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateActor() throws Exception {
        mockMvc.perform(patch(URL + "/" + actorDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnUpdateActor() throws Exception {
        repository.deleteAll();

        ActorDTO patch = new ActorDTO();
        patch.setGender((byte) 0);

        mockMvc.perform(patch(URL + "/" + actorDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteActor() throws Exception {
        mockMvc.perform(delete(URL + "/" + actorDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteActor() throws Exception {
        mockMvc.perform(delete(URL + "/" + actorDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteActor() throws Exception {
        mockMvc.perform(delete(URL + "/" + actorDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnUpdateActorOnDeleteActor() throws Exception {
        repository.deleteAll();

        mockMvc.perform(delete(URL + "/" + actorDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}