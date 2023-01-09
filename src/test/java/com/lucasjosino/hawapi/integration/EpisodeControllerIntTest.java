package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestConfig
public class CharacterControllerIntTest extends DatabaseContainerInitializer {

    private static final CharacterModel character = getSingleCharacter();

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        characterRepository.deleteAll();
    }

    @Test
    public void shouldCreateCharacter() throws Exception {
        CharacterModel characterToBeSaved = getNewCharacter();

        mockMvc.perform(post("/api/v1/characters")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(characterToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.first_name").value(characterToBeSaved.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(characterToBeSaved.getLastName()))
                .andExpect(jsonPath("$.gender").value(characterToBeSaved.getGender().toString()))
                .andExpect(jsonPath("$.actor").value(characterToBeSaved.getActor()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedCreateCharacter() throws Exception {
        CharacterModel characterToBeSaved = getNewCharacter();

        mockMvc.perform(post("/api/v1/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(characterToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenCreateCharacter() throws Exception {
        CharacterModel characterToBeSaved = getNewCharacter();

        mockMvc.perform(post("/api/v1/characters")
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(characterToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnCharacterByUUID() throws Exception {
        characterRepository.saveAll(getCharacters());

        mockMvc.perform(get("/api/v1/characters/" + character.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(character.getUuid().toString()))
                .andExpect(jsonPath("$.href").value(character.getHref()))
                .andExpect(jsonPath("$.first_name").value(character.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(character.getLastName()))
                .andExpect(jsonPath("$.gender").value(character.getGender().toString()))
                .andExpect(jsonPath("$.actor").value(character.getActor()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundCharacter() throws Exception {
        mockMvc.perform(get("/api/v1/characters/" + character.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfCharacters() throws Exception {
        characterRepository.saveAll(getCharacters());

        mockMvc.perform(get("/api/v1/characters"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyListOfCharacters() throws Exception {
        mockMvc.perform(get("/api/v1/characters"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnListOfCharactersWithFilter() throws Exception {
        CharacterModel character = getCharacters().get(1);

        characterRepository.saveAll(getCharacters());

        mockMvc.perform(get("/api/v1/characters")
                        .param("gender", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(character.getUuid().toString()))
                .andExpect(jsonPath("$[0].href").value(character.getHref()))
                .andExpect(jsonPath("$[0].first_name").value(character.getFirstName()))
                .andExpect(jsonPath("$[0].last_name").value(character.getLastName()))
                .andExpect(jsonPath("$[0].gender").value(character.getGender().toString()))
                .andExpect(jsonPath("$[0].actor").value(character.getActor()))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnListOfCharactersWithSortFilter() throws Exception {
        List<CharacterModel> reversedCharacters = new ArrayList<>(getCharacters());
        Collections.reverse(reversedCharacters);

        characterRepository.saveAll(getCharacters());

        mockMvc.perform(get("/api/v1/characters")
                        .param("sort", "DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(reversedCharacters.get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(reversedCharacters.get(0).getUuid().toString()));
    }

    @Test
    public void shouldReturnListOfCharactersWithOrderFilter() throws Exception {
        characterRepository.saveAll(getCharacters());

        mockMvc.perform(get("/api/v1/characters")
                        .param("order", "first_name")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(getCharacters().get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(getCharacters().get(0).getUuid().toString()));
    }

    @Test
    public void shouldUpdateCharacter() throws Exception {
        CharacterModel characterToBeUpdated = new CharacterModel();
        characterToBeUpdated.setLastName("Moa");

        characterRepository.saveAll(getCharacters());

        mockMvc.perform(patch("/api/v1/characters/" + character.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(characterToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundUpdateCharacter() throws Exception {
        CharacterModel characterToBeUpdated = new CharacterModel();
        characterToBeUpdated.setLastName("Moa");

        mockMvc.perform(patch("/api/v1/characters/" + character.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(characterToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedUpdateCharacter() throws Exception {
        CharacterModel characterToBeUpdated = new CharacterModel();
        characterToBeUpdated.setLastName("Moa");

        characterRepository.saveAll(getCharacters());

        mockMvc.perform(patch("/api/v1/characters/" + character.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(characterToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenUpdateCharacter() throws Exception {
        CharacterModel characterToBeUpdated = new CharacterModel();
        characterToBeUpdated.setLastName("Moa");

        characterRepository.saveAll(getCharacters());

        mockMvc.perform(patch("/api/v1/characters/" + character.getUuid())
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(characterToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeleteCharacter() throws Exception {
        characterRepository.saveAll(getCharacters());

        mockMvc.perform(delete("/api/v1/characters/" + character.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteCharacter() throws Exception {
        mockMvc.perform(delete("/api/v1/characters/" + character.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteCharacter() throws Exception {
        mockMvc.perform(delete("/api/v1/characters/" + character.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenDeleteCharacter() throws Exception {
        mockMvc.perform(delete("/api/v1/characters/" + character.getUuid())
                        .with(getDevAuth())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
