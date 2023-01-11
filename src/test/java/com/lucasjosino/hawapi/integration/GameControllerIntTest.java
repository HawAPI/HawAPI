package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.repositories.GameRepository;
import org.junit.jupiter.api.AfterAll;
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
public class GameControllerIntTest extends DatabaseContainerInitializer {

    private static final GameModel game = getSingleGame();

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        gameRepository.deleteAll();
    }

    @AfterAll
    public void cleanUp() {
        gameRepository.deleteAll();
    }

    @Test
    public void shouldCreateGame() throws Exception {
        GameModel gameToBeSaved = getNewGame();

        mockMvc.perform(post("/api/v1/games")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(gameToBeSaved.getName()))
                .andExpect(jsonPath("$.release_date").value(gameToBeSaved.getReleaseDate().toString()))
                .andExpect(jsonPath("$.url").value(gameToBeSaved.getUrl()))
                .andExpect(jsonPath("$.trailer").value(gameToBeSaved.getTrailer()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedCreateGame() throws Exception {
        GameModel gameToBeSaved = getNewGame();

        mockMvc.perform(post("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenCreateGame() throws Exception {
        GameModel gameToBeSaved = getNewGame();

        mockMvc.perform(post("/api/v1/games")
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnGameByUUID() throws Exception {
        gameRepository.saveAll(getGames());

        mockMvc.perform(get("/api/v1/games/" + game.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(game.getUuid().toString()))
                .andExpect(jsonPath("$.href").value(game.getHref()))
                .andExpect(jsonPath("$.name").value(game.getName()))
                .andExpect(jsonPath("$.release_date").value(game.getReleaseDate().toString()))
                .andExpect(jsonPath("$.url").value(game.getUrl()))
                .andExpect(jsonPath("$.trailer").value(game.getTrailer()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundGame() throws Exception {
        mockMvc.perform(get("/api/v1/games/" + game.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfGames() throws Exception {
        gameRepository.saveAll(getGames());

        mockMvc.perform(get("/api/v1/games"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyListOfGames() throws Exception {
        mockMvc.perform(get("/api/v1/games"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnListOfGamesWithFilter() throws Exception {
        GameModel game = getGames().get(1);

        gameRepository.saveAll(getGames());

        mockMvc.perform(get("/api/v1/games")
                        .param("gender", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(game.getUuid().toString()))
                .andExpect(jsonPath("$[0].href").value(game.getHref()))
                .andExpect(jsonPath("$.name").value(game.getName()))
                .andExpect(jsonPath("$.release_date").value(game.getReleaseDate()))
                .andExpect(jsonPath("$.url").value(game.getUrl()))
                .andExpect(jsonPath("$.trailer").value(game.getTrailer()))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnListOfGamesWithSortFilter() throws Exception {
        List<GameModel> reversedGames = new ArrayList<>(getGames());
        Collections.reverse(reversedGames);

        gameRepository.saveAll(getGames());

        mockMvc.perform(get("/api/v1/games")
                        .param("sort", "DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(reversedGames.get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(reversedGames.get(0).getUuid().toString()));
    }

    @Test
    public void shouldReturnListOfGamesWithOrderFilter() throws Exception {
        gameRepository.saveAll(getGames());

        mockMvc.perform(get("/api/v1/games")
                        .param("order", "name")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(getGames().get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(getGames().get(0).getUuid().toString()));
    }

    @Test
    public void shouldUpdateGame() throws Exception {
        GameModel gameToBeUpdated = new GameModel();
        gameToBeUpdated.setName("Moa");

        gameRepository.saveAll(getGames());

        mockMvc.perform(patch("/api/v1/games/" + game.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundUpdateGame() throws Exception {
        GameModel gameToBeUpdated = new GameModel();
        gameToBeUpdated.setName("Moa");

        mockMvc.perform(patch("/api/v1/games/" + game.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedUpdateGame() throws Exception {
        GameModel gameToBeUpdated = new GameModel();
        gameToBeUpdated.setName("Moa");

        gameRepository.saveAll(getGames());

        mockMvc.perform(patch("/api/v1/games/" + game.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenUpdateGame() throws Exception {
        GameModel gameToBeUpdated = new GameModel();
        gameToBeUpdated.setName("Moa");

        gameRepository.saveAll(getGames());

        mockMvc.perform(patch("/api/v1/games/" + game.getUuid())
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeleteGame() throws Exception {
        gameRepository.saveAll(getGames());

        mockMvc.perform(delete("/api/v1/games/" + game.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteGame() throws Exception {
        mockMvc.perform(delete("/api/v1/games/" + game.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteGame() throws Exception {
        mockMvc.perform(delete("/api/v1/games/" + game.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenDeleteGame() throws Exception {
        mockMvc.perform(delete("/api/v1/games/" + game.getUuid())
                        .with(getDevAuth())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
