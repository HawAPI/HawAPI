package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
class LocationTranslationRepositoryTest extends DatabaseContainerInitializer {

    private LocationTranslation translationModel;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationTranslationRepository translationRepository;

    @BeforeEach
    void setUp() {
        LocationModel locationModel = new LocationModel();
        locationModel.setUuid(UUID.randomUUID());
        locationModel.setHref("/api/v1/locations/" + locationModel.getUuid());
        locationModel.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        locationModel.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        locationModel.setSources(Arrays.asList("https://example.com", "https://example.com"));
        locationModel.setCreatedAt(LocalDateTime.now());
        locationModel.setUpdatedAt(LocalDateTime.now());

        translationModel = new LocationTranslation();
        translationModel.setLocationUuid(locationModel.getUuid());
        translationModel.setLanguage("en-US");
        translationModel.setName("Lorem Ipsum");
        translationModel.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        entityManager.persist(locationModel);
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
    void shouldFindAllByLocationUuid() {
        List<LocationTranslation> res = translationRepository.findAllByLocationUuid(translationModel.getLocationUuid());
        LocationTranslation first = res.get(0);

        assertEquals(1, res.size());
        assertEquals(translationModel.getLocationUuid(), first.getLocationUuid());
        assertEquals(translationModel.getName(), first.getName());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenNoLocationFoundShouldReturnEmptyListOnFindAllByLocationUuid() {
        deleteAndFlushRepositories();

        List<LocationTranslation> res = translationRepository.findAllByLocationUuid(translationModel.getLocationUuid());

        assertEquals(0, res.size());
    }

    @Test
    void shouldFindAllByLocationUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<LocationTranslation> res = translationRepository.findAllByLocationUuid(
                translationModel.getLocationUuid(),
                pageable
        );
        LocationTranslation first = res.getContent().get(0);

        assertEquals(1, res.getContent().size());
        assertEquals(translationModel.getLocationUuid(), first.getLocationUuid());
        assertEquals(translationModel.getName(), first.getName());
        assertEquals(translationModel.getDescription(), first.getDescription());
        assertEquals(translationModel.getLanguage(), first.getLanguage());
    }

    @Test
    void whenHigherPageIsProvidedShouldReturnEmptyPageOnFindAllByLocationUuidUsingPageable() {
        PageRequest pageable = PageRequest.of(1, 1);
        Page<LocationTranslation> res = translationRepository.findAllByLocationUuid(
                translationModel.getLocationUuid(),
                pageable
        );

        assertEquals(0, res.getContent().size());
    }

    @Test
    void shouldFindByLocationUuidAndLanguage() {
        Optional<LocationTranslation> res = translationRepository.findByLocationUuidAndLanguage(
                translationModel.getLocationUuid(),
                "en-US"
        );

        assertTrue(res.isPresent());
        assertEquals(translationModel.getLocationUuid(), res.get().getLocationUuid());
        assertEquals(translationModel.getName(), res.get().getName());
        assertEquals(translationModel.getDescription(), res.get().getDescription());
        assertEquals(translationModel.getLanguage(), res.get().getLanguage());
    }

    @Test
    void whenNoLocationTranslationLanguageFoundShouldReturnEmptyOptionalOnFindByLocationUuidAndLanguage() {
        Optional<LocationTranslation> res = translationRepository.findByLocationUuidAndLanguage(
                translationModel.getLocationUuid(),
                "pt-BR"
        );

        assertFalse(res.isPresent());
    }

    @Test
    void shouldReturnTrueOnExistsByLocationUuidAndLanguage() {
        boolean res = translationRepository.existsByLocationUuidAndLanguage(
                translationModel.getLocationUuid(),
                "en-US"
        );

        assertTrue(res);
    }

    @Test
    void whenNoLocationTranslationLanguageFoundShouldReturnFalseOnExistsByLocationUuidAndLanguage() {
        boolean res = translationRepository.existsByLocationUuidAndLanguage(
                translationModel.getLocationUuid(),
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
    void whenNoLocationTranslationLanguageFoundShouldReturnFalseOnExistsByTitleAndLanguage() {
        boolean res = translationRepository.existsByNameAndLanguage(
                translationModel.getName(),
                "pt-BR"
        );

        assertFalse(res);
    }

    @Test
    void shouldDeleteByLocationUuidAndLanguage() {
        translationRepository.deleteByLocationUuidAndLanguage(translationModel.getLocationUuid(), "en-US");

        assertEquals(0, translationRepository.count());
    }
}