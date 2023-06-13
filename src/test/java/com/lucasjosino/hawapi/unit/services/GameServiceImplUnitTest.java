package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.GameFilter;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.GameRepository;
import com.lucasjosino.hawapi.services.impl.GameServiceImpl;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertGameEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class GameServiceImplUnitTest {

    private static final GameModel game = getSingleGame();

    @Mock
    private ServiceUtils utils;

    @Mock
    private OpenAPIProperty config;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    public void shouldCreateGame() {
        GameModel newGame = getNewGame();
        when(gameRepository.save(any(GameModel.class))).thenReturn(newGame);

        GameModel res = gameService.save(newGame);

        assertGameEquals(newGame, res);
        verify(gameRepository, times(1)).save(any(GameModel.class));
    }

    @Test
    public void shouldReturnGameByUUID() {
        GameModel newGame = getNewGame();
        when(gameRepository.findById(any(UUID.class))).thenReturn(Optional.of(newGame));

        GameModel res = gameService.findByUUID(newGame.getUuid());

        assertGameEquals(newGame, res);
        verify(gameRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundGame() {
        GameModel newGame = getNewGame();
        when(gameRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> gameService.findByUUID(newGame.getUuid()));
        verify(gameRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfGames() {
        when(gameRepository.findAll(Mockito.<Example<GameModel>>any())).thenReturn(getGames());

        List<GameModel> res = gameService.findAll(null);

        assertEquals(2, res.size());
        verify(gameRepository, times(1)).findAll(Mockito.<Example<GameModel>>any());
    }

    @Test
    public void shouldReturnEmptyListOfGames() {
        when(gameRepository.findAll(Mockito.<Example<GameModel>>any())).thenReturn(new ArrayList<>());

        List<GameModel> res = gameService.findAll(null);

        assertEquals(Collections.EMPTY_LIST, res);
        verify(gameRepository, times(1)).findAll(Mockito.<Example<GameModel>>any());
    }

    @Test
    public void shouldReturnListOfGamesWithFilter() {
        List<GameModel> filteredGameList = new ArrayList<>(Collections.singletonList(game));
        when(gameRepository.findAll(Mockito.<Example<GameModel>>any())).thenReturn(filteredGameList);

        GameFilter filter = Mockito.mock(GameFilter.class);
        List<GameModel> res = gameService.findAll(filter);

        assertEquals(1, res.size());
        verify(gameRepository, times(1)).findAll(Mockito.<Example<GameModel>>any());
    }

    @Test
    public void shouldUpdateGame() throws JsonPatchException, JsonProcessingException {
        when(gameRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getGames().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getGames().get(0));
        when(gameRepository.save(any(GameModel.class))).thenReturn(getGames().get(0));

        gameService.patch(game.getUuid(), mapper().valueToTree(game));

        verify(gameRepository, times(1)).save(any(GameModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateGame() {
        doThrow(ItemNotFoundException.class)
                .when(gameRepository).findById(any(UUID.class));

        JsonNode node = mapper().valueToTree(game);

        assertThrows(ItemNotFoundException.class, () -> gameService.patch(game.getUuid(), node));
        verify(gameRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteGame() {
        when(gameRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(gameRepository).deleteById(any(UUID.class));

        gameService.delete(game.getUuid());

        verify(gameRepository, times(1)).existsById(any(UUID.class));
        verify(gameRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteGame() {
        when(gameRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(gameRepository).deleteById(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> gameService.delete(game.getUuid()));
        verify(gameRepository, times(1)).existsById(any(UUID.class));
        verify(gameRepository, times(1)).deleteById(any(UUID.class));
    }
}
