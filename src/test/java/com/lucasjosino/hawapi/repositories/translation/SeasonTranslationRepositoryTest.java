package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
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
class SeasonTranslationRepositoryTest extends DatabaseContainerInitializer {

    private SeasonTranslation translationModel;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SeasonRepository repository;

    @Autowired
    private SeasonTranslationRepository translationRepository;

    @BeforeEach
    void setUp() {
        SeasonModel seasonModel = new SeasonModel();
        seasonModel.setUuid(UUID.randomUUID());
        seasonModel.setHref("/api/v1/seasons/" + seasonModel.getUuid());
        seasonModel.setLanguages(Collections.singletonList("Lorem"));
        seasonModel.setDurationTotal(215398753);
        seasonModel.setSeasonNum((byte) 2);
        seasonModel.setReleaseDate(LocalDate.now());
        seasonModel.setNextSeason("/api/v1/seasons/3");
        seasonModel.setPrevSeason("/api/v1/seasons/1");
        seasonModel.setEpisodes(Arrays.asList("/api/v1/episodes/1", "/api/v1/episodes/2", "/api/v1/episodes/3"));
        seasonModel.setSoundtracks(Arrays.asList("/api/v1/soundtracks/1",
                "/api/v1/soundtracks/2",
                "/api/v1/soundtracks/3"
        ));
        seasonModel.setBudget(218459);
        seasonModel.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        seasonModel.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        seasonModel.setSources(Arrays.asList("https://example.com", "https://example.com"));
        seasonModel.setCreatedAt(LocalDateTime.now());
        seasonModel.setUpdatedAt(LocalDateTime.now());

        translationModel = new SeasonTranslation();
        translationModel.setSeasonUuid(seasonModel.getUuid());
        translationModel.setLanguage("en-US");
        translationModel.setTitle("Lorem Ipsum");
        translationModel.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translationModel.setGenres(Arrays.asList("gen1", "gen2", "gen3"));
        translationModel.setTrailers(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));

        entityManager.persist(seasonModel);
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
    void shouldFindAllBySeasonUuid() {
        List<SeasonTranslation> res = translationRepository.findAllBySeasonUuid(translationModel.getSeasonUuid());
        SeasonTranslation first = res.get(0);

        assertEquals(1, res.size());
        assertEquals(translationModel.getSeasonUuid(), first.getSeasonUuid());
        assertEquals(translationModel.getTitle(), first.getTitle());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenNoSeasonFoundShouldReturnEmptyListOnFindAllBySeasonUuid() {
        deleteAndFlushRepositories();

        List<SeasonTranslation> res = translationRepository.findAllBySeasonUuid(translationModel.getSeasonUuid());

        assertEquals(0, res.size());
    }

    @Test
    void shouldFindAllBySeasonUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<SeasonTranslation> res = translationRepository.findAllBySeasonUuid(
                translationModel.getSeasonUuid(),
                pageable
        );
        SeasonTranslation first = res.getContent().get(0);

        assertEquals(1, res.getContent().size());
        assertEquals(translationModel.getSeasonUuid(), first.getSeasonUuid());
        assertEquals(translationModel.getTitle(), first.getTitle());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenHigherPageIsProvidedShouldReturnEmptyPageOnFindAllBySeasonUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(1, 1);
        Page<SeasonTranslation> res = translationRepository.findAllBySeasonUuid(
                translationModel.getSeasonUuid(),
                pageable
        );

        assertEquals(0, res.getContent().size());
    }

    @Test
    void shouldFindBySeasonUuidAndLanguage() {
        Optional<SeasonTranslation> res = translationRepository.findBySeasonUuidAndLanguage(
                translationModel.getSeasonUuid(),
                "en-US"
        );

        assertTrue(res.isPresent());
        assertEquals(translationModel.getSeasonUuid(), res.get().getSeasonUuid());
        assertEquals(translationModel.getTitle(), res.get().getTitle());
        assertEquals(translationModel.getDescription(), res.get().getDescription());
        assertEquals(translationModel.getLanguage(), res.get().getLanguage());
    }

    @Test
    void whenNoSeasonTranslationLanguageFoundShouldReturnEmptyOptionalOnFindBySeasonUuidAndLanguage() {
        Optional<SeasonTranslation> res = translationRepository.findBySeasonUuidAndLanguage(
                translationModel.getSeasonUuid(),
                "pt-BR"
        );

        assertFalse(res.isPresent());
    }

    @Test
    void shouldReturnTrueOnExistsBySeasonUuidAndLanguage() {
        boolean res = translationRepository.existsBySeasonUuidAndLanguage(
                translationModel.getSeasonUuid(),
                "en-US"
        );

        assertTrue(res);
    }

    @Test
    void whenNoSeasonTranslationLanguageFoundShouldReturnFalseOnExistsBySeasonUuidAndLanguage() {
        boolean res = translationRepository.existsBySeasonUuidAndLanguage(
                translationModel.getSeasonUuid(),
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
    void whenNoSeasonTranslationLanguageFoundShouldReturnFalseOnExistsByTitleAndLanguage() {
        boolean res = translationRepository.existsByTitleAndLanguage(
                translationModel.getTitle(),
                "pt-BR"
        );

        assertFalse(res);
    }

    @Test
    void shouldDeleteBySeasonUuidAndLanguage() {
        translationRepository.deleteBySeasonUuidAndLanguage(translationModel.getSeasonUuid(), "en-US");

        assertEquals(0, translationRepository.count());
    }
}