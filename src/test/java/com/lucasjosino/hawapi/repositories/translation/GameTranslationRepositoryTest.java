package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.translations.GameTranslation;
import com.lucasjosino.hawapi.repositories.GameRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
class GameTranslationRepositoryTest extends DatabaseContainerInitializer {

    private GameTranslation translationModel;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GameRepository repository;

    @Autowired
    private GameTranslationRepository translationRepository;

    @BeforeEach
    void setUp() {
        GameModel gameModel = new GameModel();
        gameModel.setUuid(UUID.randomUUID());
        gameModel.setHref("/api/v1/games/" + gameModel.getUuid());
        gameModel.setLanguages(Collections.singletonList("Lorem"));
        gameModel.setReleaseDate(LocalDate.now());
        gameModel.setWebsite("https://example.com");
        gameModel.setPlaytime(210574565);
        gameModel.setAgeRating("100+");
        gameModel.setStores(Arrays.asList("https://store.example.com", "https://store.example.com"));
        gameModel.setModes(Arrays.asList("Single Player", "Multi Player"));
        gameModel.setPublishers(Arrays.asList("Lorem", "Ipsum"));
        gameModel.setDevelopers(Arrays.asList("Lorem", "Ipsum"));
        gameModel.setPlatforms(Arrays.asList("Android", "iOS"));
        gameModel.setGenres(Arrays.asList("Lorem", "Ipsum"));
        gameModel.setTags(Arrays.asList("horror", "suspense"));
        gameModel.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        gameModel.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        gameModel.setSources(Arrays.asList("https://example.com", "https://example.com"));
        gameModel.setCreatedAt(LocalDateTime.now());
        gameModel.setUpdatedAt(LocalDateTime.now());

        translationModel = new GameTranslation();
        translationModel.setGameUuid(gameModel.getUuid());
        translationModel.setLanguage("en-US");
        translationModel.setName("Lorem Ipsum");
        translationModel.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translationModel.setTrailer("https://youtube.com/watch?v=1");

        entityManager.persist(gameModel);
        entityManager.persist(translationModel);
    }

    @AfterEach
    void tearDown() {
        deleteAndFlushRepositories();
    }

    private void deleteAndFlushRepositories() {
        entityManager.clear();
        entityManager.flush();
        translationRepository.deleteAll();
        translationRepository.deleteAll();
    }

    @Test
    void shouldFindAllByGameUuid() {
        List<GameTranslation> res = translationRepository.findAllByGameUuid(translationModel.getGameUuid());
        GameTranslation first = res.get(0);

        assertEquals(1, res.size());
        assertEquals(translationModel.getGameUuid(), first.getGameUuid());
        assertEquals(translationModel.getName(), first.getName());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenNoGameFoundShouldReturnEmptyListOnFindAllByGameUuid() {
        deleteAndFlushRepositories();

        List<GameTranslation> res = translationRepository.findAllByGameUuid(translationModel.getGameUuid());

        assertEquals(0, res.size());
    }

    @Test
    void shouldFindAllByGameUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<GameTranslation> res = translationRepository.findAllByGameUuid(
                translationModel.getGameUuid(),
                pageable
        );
        GameTranslation first = res.getContent().get(0);

        assertEquals(1, res.getContent().size());
        assertEquals(translationModel.getGameUuid(), first.getGameUuid());
        assertEquals(translationModel.getName(), first.getName());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenHigherPageIsProvidedShouldReturnEmptyPageOnFindAllByGameUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(1, 1);
        Page<GameTranslation> res = translationRepository.findAllByGameUuid(
                translationModel.getGameUuid(),
                pageable
        );

        assertEquals(0, res.getContent().size());
    }

    @Test
    void shouldFindByGameUuidAndLanguage() {
        Optional<GameTranslation> res = translationRepository.findByGameUuidAndLanguage(
                translationModel.getGameUuid(),
                "en-US"
        );

        assertTrue(res.isPresent());
        assertEquals(translationModel.getGameUuid(), res.get().getGameUuid());
        assertEquals(translationModel.getName(), res.get().getName());
        assertEquals(translationModel.getDescription(), res.get().getDescription());
        assertEquals(translationModel.getLanguage(), res.get().getLanguage());
    }

    @Test
    void whenNoGameTranslationLanguageFoundShouldReturnEmptyOptionalOnFindByGameUuidAndLanguage() {
        Optional<GameTranslation> res = translationRepository.findByGameUuidAndLanguage(
                translationModel.getGameUuid(),
                "pt-BR"
        );

        assertFalse(res.isPresent());
    }

    @Test
    void shouldReturnTrueOnExistsByGameUuidAndLanguage() {
        boolean res = translationRepository.existsByGameUuidAndLanguage(
                translationModel.getGameUuid(),
                "en-US"
        );

        assertTrue(res);
    }

    @Test
    void whenNoGameTranslationLanguageFoundShouldReturnFalseOnExistsByGameUuidAndLanguage() {
        boolean res = translationRepository.existsByGameUuidAndLanguage(
                translationModel.getGameUuid(),
                "pt-BR"
        );

        assertFalse(res);
    }

    @Test
    void shouldReturnTrueOnExistsByTitleAndLanguage() {
        boolean res = translationRepository.existsByNameAndLanguage(
                translationModel.getName(),
                "en-US"
        );

        assertTrue(res);
    }

    @Test
    void whenNoGameTranslationLanguageFoundShouldReturnFalseOnExistsByTitleAndLanguage() {
        boolean res = translationRepository.existsByNameAndLanguage(
                translationModel.getName(),
                "pt-BR"
        );

        assertFalse(res);
    }

    @Test
    void shouldDeleteByGameUuidAndLanguage() {
        translationRepository.deleteByGameUuidAndLanguage(translationModel.getGameUuid(), "en-US");

        assertEquals(0, translationRepository.count());
    }
}