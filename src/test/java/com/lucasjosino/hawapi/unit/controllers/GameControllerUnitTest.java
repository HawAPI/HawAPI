package com.lucasjosino.hawapi.unit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.GameController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.GameFilter;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.services.GameService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertGameEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class GameControllerUnitTest {

    private static final GameModel game = getSingleGame();

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @Test
    public void shouldCreateGame() {
        GameModel newGame = getNewGame();
        when(gameService.save(any(GameModel.class))).thenReturn(newGame);

        ResponseEntity<GameModel> res = gameController.save(newGame);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertGameEquals(newGame, res);
        verify(gameService, times(1)).save(any(GameModel.class));
    }

    @Test
    public void shouldReturnGameByUUID() {
        GameModel newGame = getNewGame();
        when(gameService.findByUUID(any(UUID.class))).thenReturn(newGame);

        ResponseEntity<GameModel> res = gameController.findByUUID(newGame.getUuid());

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertGameEquals(newGame, res);
        verify(gameService, times(1)).findByUUID(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundGame() {
        GameModel newGame = getNewGame();
        when(gameService.findByUUID(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> gameController.findByUUID(newGame.getUuid()));
        verify(gameService, times(1)).findByUUID(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfGames() {
        when(gameService.findAll(null)).thenReturn(getGames());

        ResponseEntity<List<GameModel>> res = gameController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(2, res.getBody().size());
        verify(gameService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnEmptyListOfGames() {
        when(gameService.findAll(null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<GameModel>> res = gameController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, res.getBody());
        verify(gameService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnListOfGamesWithFilter() {
        List<GameModel> filteredGameList = new ArrayList<>(Collections.singletonList(game));
        when(gameService.findAll(any(GameFilter.class))).thenReturn(filteredGameList);

        GameFilter filter = Mockito.mock(GameFilter.class);
        ResponseEntity<List<GameModel>> res = gameController.findAll(filter);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(1, res.getBody().size());
        verify(gameService, times(1)).findAll(any(GameFilter.class));
    }

    @Test
    public void shouldUpdateGame() throws JsonPatchException, JsonProcessingException {
        doNothing()
                .when(gameService).patch(any(UUID.class), any(JsonNode.class));

        ResponseEntity<Void> res = gameController.patch(game.getUuid(), mapper().valueToTree(game));

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(gameService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateGame() throws JsonPatchException, JsonProcessingException {
        doThrow(ItemNotFoundException.class)
                .when(gameService).patch(any(UUID.class), any(JsonNode.class));

        JsonNode node = mapper().valueToTree(game);

        assertThrows(ItemNotFoundException.class, () -> gameController.patch(game.getUuid(), node));
        verify(gameService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldDeleteGame() {
        doNothing()
                .when(gameService).delete(any(UUID.class));

        ResponseEntity<Void> res = gameController.delete(game.getUuid());

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(gameService, times(1)).delete(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteGame() {
        doThrow(ItemNotFoundException.class)
                .when(gameService).delete(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> gameController.delete(game.getUuid()));
        verify(gameService, times(1)).delete(any(UUID.class));
    }
}
