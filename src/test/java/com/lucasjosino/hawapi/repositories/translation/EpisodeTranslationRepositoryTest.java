package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
class EpisodeTranslationRepositoryTest extends DatabaseContainerInitializer {

    private EpisodeTranslation translationModel;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EpisodeRepository repository;

    @Autowired
    private EpisodeTranslationRepository translationRepository;

    @BeforeEach
    void setUp() {
        EpisodeModel episodeModel = new EpisodeModel();
        episodeModel.setUuid(UUID.randomUUID());
        episodeModel.setHref("/api/v1/episodes/" + episodeModel.getUuid());
        episodeModel.setLanguages(Collections.singletonList("Lorem"));
        episodeModel.setDuration(12482342);
        episodeModel.setEpisodeNum((byte) 2);
        episodeModel.setNextEpisode("/api/v1/episodes/3");
        episodeModel.setPrevEpisode("/api/v1/episodes/1");
        episodeModel.setSeason("/api/v1/seasons/1");
        episodeModel.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        episodeModel.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        episodeModel.setSources(Arrays.asList("https://example.com", "https://example.com"));
        episodeModel.setCreatedAt(LocalDateTime.now());
        episodeModel.setUpdatedAt(LocalDateTime.now());

        translationModel = new EpisodeTranslation();
        translationModel.setEpisodeUuid(episodeModel.getUuid());
        translationModel.setLanguage("en-US");
        translationModel.setTitle("Lorem Ipsum");
        translationModel.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        entityManager.persist(episodeModel);
        entityManager.persist(translationModel);
    }

    @AfterEach
    void tearDown() {
        deleteAndFlushRepositories();
    }

    private void deleteAndFlushRepositories() {
        entityManager.clear();
        entityManager.flush();
        repository.deleteAll();
        translationRepository.deleteAll();
    }

    @Test
    void shouldFindAllByEpisodeUuid() {
        List<EpisodeTranslation> res = translationRepository.findAllByEpisodeUuid(translationModel.getEpisodeUuid());
        EpisodeTranslation first = res.get(0);

        assertEquals(1, res.size());
        assertEquals(translationModel.getEpisodeUuid(), first.getEpisodeUuid());
        assertEquals(translationModel.getTitle(), first.getTitle());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenNoEpisodeFoundShouldReturnEmptyListOnFindAllByEpisodeUuid() {
        deleteAndFlushRepositories();

        List<EpisodeTranslation> res = translationRepository.findAllByEpisodeUuid(translationModel.getEpisodeUuid());

        assertEquals(0, res.size());
    }

    @Test
    void shouldFindAllByEpisodeUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<EpisodeTranslation> res = translationRepository.findAllByEpisodeUuid(
                translationModel.getEpisodeUuid(),
                pageable
        );
        EpisodeTranslation first = res.getContent().get(0);

        assertEquals(1, res.getContent().size());
        assertEquals(translationModel.getEpisodeUuid(), first.getEpisodeUuid());
        assertEquals(translationModel.getTitle(), first.getTitle());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenHigherPageIsProvidedShouldReturnEmptyPageOnFindAllByEpisodeUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(1, 1);
        Page<EpisodeTranslation> res = translationRepository.findAllByEpisodeUuid(
                translationModel.getEpisodeUuid(),
                pageable
        );

        assertEquals(0, res.getContent().size());
    }

    @Test
    void shouldFindByEpisodeUuidAndLanguage() {
        Optional<EpisodeTranslation> res = translationRepository.findByEpisodeUuidAndLanguage(
                translationModel.getEpisodeUuid(),
                "en-US"
        );

        assertTrue(res.isPresent());
        assertEquals(translationModel.getEpisodeUuid(), res.get().getEpisodeUuid());
        assertEquals(translationModel.getTitle(), res.get().getTitle());
        assertEquals(translationModel.getDescription(), res.get().getDescription());
        assertEquals(translationModel.getLanguage(), res.get().getLanguage());
    }

    @Test
    void whenNoEpisodeTranslationLanguageFoundShouldReturnEmptyOptionalOnFindByEpisodeUuidAndLanguage() {
        Optional<EpisodeTranslation> res = translationRepository.findByEpisodeUuidAndLanguage(
                translationModel.getEpisodeUuid(),
                "pt-BR"
        );

        assertFalse(res.isPresent());
    }

    @Test
    void shouldReturnTrueOnExistsByEpisodeUuidAndLanguage() {
        boolean res = translationRepository.existsByEpisodeUuidAndLanguage(
                translationModel.getEpisodeUuid(),
                "en-US"
        );

        assertTrue(res);
    }

    @Test
    void whenNoEpisodeTranslationLanguageFoundShouldReturnFalseOnExistsByEpisodeUuidAndLanguage() {
        boolean res = translationRepository.existsByEpisodeUuidAndLanguage(
                translationModel.getEpisodeUuid(),
                "pt-BR"
        );

        assertFalse(res);
    }

    @Test
    void shouldReturnTrueOnExistsByTitleAndLanguage() {
        boolean res = translationRepository.existsByTitleAndLanguage(
                translationModel.getTitle(),
                "en-US"
        );

        assertTrue(res);
    }

    @Test
    void whenNoEpisodeTranslationLanguageFoundShouldReturnFalseOnExistsByTitleAndLanguage() {
        boolean res = translationRepository.existsByTitleAndLanguage(
                translationModel.getTitle(),
                "pt-BR"
        );

        assertFalse(res);
    }

    @Test
    void shouldDeleteByEpisodeUuidAndLanguage() {
        translationRepository.deleteByEpisodeUuidAndLanguage(translationModel.getEpisodeUuid(), "en-US");

        assertEquals(0, translationRepository.count());
    }
}