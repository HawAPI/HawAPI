package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.OverviewModel;
import com.lucasjosino.hawapi.models.translations.OverviewTranslation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class OverviewTranslationRepositoryTest extends DatabaseContainerInitializer {

    private OverviewTranslation translationModel;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OverviewTranslationRepository translationRepository;

    @BeforeEach
    void setUp() {
        OverviewModel overview = new OverviewModel();
        overview.setUuid(UUID.randomUUID());
        overview.setHref("/api/v1/overview/" + overview.getUuid());
        overview.setCreators(Collections.singletonList("Lorem"));
        overview.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        overview.setSources(Arrays.asList("https://example.com", "https://example.com"));
        overview.setCreatedAt(LocalDateTime.now());
        overview.setUpdatedAt(LocalDateTime.now());

        translationModel = new OverviewTranslation();
        translationModel.setOverviewUuid(overview.getUuid());
        translationModel.setLanguage("en-US");
        translationModel.setTitle("Lorem Ipsum");
        translationModel.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        entityManager.persist(overview);
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
    }

    @Test
    void shouldFindByLanguage() {
        Optional<OverviewTranslation> res = translationRepository.findByLanguage("en-US");
        assertTrue(res.isPresent());

        OverviewTranslation value = res.get();

        assertNotNull(value);
        assertEquals(translationModel, value);
    }

    @Test
    void whenNoLanguageFoundShouldReturnEmptyOptionalOnFindByLanguage() {
        translationRepository.deleteAll();

        Optional<OverviewTranslation> res = translationRepository.findByLanguage("pt-BR");

        assertFalse(res.isPresent());
    }

    @Test
    void shouldExistByLanguage() {
        boolean res = translationRepository.existsByLanguage("en-US");

        assertTrue(res);
    }

    @Test
    void whenNoLanguageFoundShouldReturnFalseOnExistByLanguage() {
        boolean res = translationRepository.existsByLanguage("pt-BR");

        assertFalse(res);
    }

    @Test
    void shouldDeleteByLanguage() {
        translationRepository.deleteByLanguage("en-US");

        assertEquals(translationRepository.count(), 0);
    }
}
