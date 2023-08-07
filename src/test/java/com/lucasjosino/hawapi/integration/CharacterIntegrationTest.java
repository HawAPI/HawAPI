package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
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
public class CharacterIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/characters/";

    private static final ModelMapper mapper = new ModelMapper();

    private CharacterDTO characterDTO;

    @Autowired
    private CharacterRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        characterDTO = new CharacterDTO();
        characterDTO.setUuid(UUID.randomUUID());
        characterDTO.setHref("/api/v1/characters/" + characterDTO.getUuid());
        characterDTO.setFirstName("Lorem");
        characterDTO.setLastName("Ipsum");
        characterDTO.setNicknames(Arrays.asList("lore", "locum"));
        characterDTO.setBirthDate(LocalDate.now());
        characterDTO.setDeathDate(LocalDate.now());
        characterDTO.setGender((byte) 1);
        characterDTO.setActor("/api/v1/actors/1");
        characterDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        characterDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        characterDTO.setCreatedAt(LocalDateTime.now());
        characterDTO.setUpdatedAt(LocalDateTime.now());

        CharacterModel characterModel = mapper.map(characterDTO, CharacterModel.class);
        repository.save(characterModel);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllCharacters() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "1"))
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
                .andExpect(header().string("X-Pagination-Page-Size", "0"))
                .andExpect(header().string("X-Pagination-Page-Total", "0"))
                .andExpect(header().string("X-Pagination-Item-Total", "0"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void shouldReturnRandomCharacter() throws Exception {
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(characterDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(characterDTO.getHref()))
                .andExpect(jsonPath("$.first_name").value(characterDTO.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(characterDTO.getLastName()))
                .andExpect(jsonPath("$.nicknames").isNotEmpty())
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(characterDTO.getBirthDate())))
                .andExpect(jsonPath("$.death_date").value(String.valueOf(characterDTO.getDeathDate())))
                .andExpect(jsonPath("$.gender").value(String.valueOf(characterDTO.getGender())))
                .andExpect(jsonPath("$.actor").value(characterDTO.getActor()))
                .andExpect(jsonPath("$.thumbnail").value(characterDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnRandomCharacter() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCharacterByUUID() throws Exception {
        mockMvc.perform(get(URL + "/" + characterDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(characterDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(characterDTO.getHref()))
                .andExpect(jsonPath("$.first_name").value(characterDTO.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(characterDTO.getLastName()))
                .andExpect(jsonPath("$.nicknames").isNotEmpty())
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(characterDTO.getBirthDate())))
                .andExpect(jsonPath("$.death_date").value(String.valueOf(characterDTO.getDeathDate())))
                .andExpect(jsonPath("$.gender").value(String.valueOf(characterDTO.getGender())))
                .andExpect(jsonPath("$.actor").value(characterDTO.getActor()))
                .andExpect(jsonPath("$.thumbnail").value(characterDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnCharacterByUUID() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/" + characterDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveCharacter() throws Exception {
        repository.deleteAll();

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(characterDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.first_name").value(characterDTO.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(characterDTO.getLastName()))
                .andExpect(jsonPath("$.nicknames").isNotEmpty())
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(characterDTO.getBirthDate())))
                .andExpect(jsonPath("$.death_date").value(String.valueOf(characterDTO.getDeathDate())))
                .andExpect(jsonPath("$.gender").value(String.valueOf(characterDTO.getGender())))
                .andExpect(jsonPath("$.actor").value(characterDTO.getActor()))
                .andExpect(jsonPath("$.thumbnail").value(characterDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveCharacter() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(characterDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveCharacter() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(characterDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveCharacter() throws Exception {
        characterDTO.setFirstName(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(characterDTO))
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
    void shouldUpdateCharacter() throws Exception {
        CharacterDTO patch = new CharacterDTO();
        patch.setGender((byte) 0);

        mockMvc.perform(patch(URL + "/" + characterDTO.getUuid())
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
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateCharacter() throws Exception {
        mockMvc.perform(patch(URL + "/" + characterDTO.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateCharacter() throws Exception {
        mockMvc.perform(patch(URL + "/" + characterDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateCharacter() throws Exception {
        mockMvc.perform(patch(URL + "/" + characterDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnUpdateCharacter() throws Exception {
        repository.deleteAll();

        CharacterDTO patch = new CharacterDTO();

        mockMvc.perform(patch(URL + "/" + characterDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCharacter() throws Exception {
        mockMvc.perform(delete(URL + "/" + characterDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteCharacter() throws Exception {
        mockMvc.perform(delete(URL + "/" + characterDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteCharacter() throws Exception {
        mockMvc.perform(delete(URL + "/" + characterDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnUpdateCharacterOnDeleteCharacter() throws Exception {
        repository.deleteAll();

        mockMvc.perform(delete(URL + "/" + characterDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
