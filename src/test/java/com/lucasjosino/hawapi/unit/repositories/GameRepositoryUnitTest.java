package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.filters.GameFilter;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.repositories.GameRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertGameEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class GameRepositoryUnitTest extends DatabaseContainerInitializer {

    private static final GameModel game = getSingleGame();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    public void setUp() {
        entityManager.clear();
        entityManager.flush();
        gameRepository.deleteAll();
        getGames().forEach(entityManager::persist);
    }

    @AfterAll
    public void cleanUp() {
        gameRepository.deleteAll();
    }

    @Test
    public void shouldCreateGame() {
        GameModel newGame = getNewGame();
        entityManager.persist(newGame);

        GameModel res = gameRepository.save(newGame);

        assertGameEquals(newGame, res);
    }

    @Test
    public void shouldReturnGameByUUID() {
        Optional<GameModel> res = gameRepository.findById(game.getUuid());

        assertTrue(res.isPresent());
        assertGameEquals(game, res.get());
    }

    @Test
    public void shouldReturnNotFoundGame() {
        entityManager.clear();
        entityManager.flush();

        Optional<GameModel> res = gameRepository.findById(game.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfGames() {
        List<GameModel> res = gameRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfGames() {
        entityManager.clear();
        entityManager.flush();

        List<GameModel> res = gameRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfGamesWithFilter() {
        ModelMapper mapper = new ModelMapper();

        GameFilter filter = new GameFilter();
        filter.setName("Lorem");

        GameModel convertedModel = mapper.map(filter, GameModel.class);
        Example<GameModel> exFilter = Example.of(convertedModel);
        List<GameModel> res = gameRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertGameEquals(game, res.get(0));
    }

    @Test
    public void shouldUpdateGame() {
        game.setName("New Lorem");
        GameModel updatedGame = gameRepository.save(game);

        assertEquals(game.getUuid(), updatedGame.getUuid());
        assertEquals(game.getName(), updatedGame.getName());

        game.setName("Lorem");
    }

    @Test
    public void shouldDeleteGame() {
        gameRepository.deleteById(game.getUuid());

        Optional<GameModel> opGame = gameRepository.findById(game.getUuid());

        assertFalse(opGame.isPresent());
    }
}
