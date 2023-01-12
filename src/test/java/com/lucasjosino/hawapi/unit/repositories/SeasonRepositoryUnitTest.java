package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.filters.SeasonFilter;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertSeasonEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.modelMapper;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class SeasonRepositoryUnitTest extends DatabaseContainerInitializer {

    private static final SeasonModel season = getSingleSeason();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SeasonRepository seasonRepository;

    @BeforeEach
    public void setUp() {
        entityManager.clear();
        entityManager.flush();
        seasonRepository.deleteAll();
        getSeasons().forEach(entityManager::persist);
    }

    @AfterAll
    public void cleanUp() {
        seasonRepository.deleteAll();
    }

    @Test
    public void shouldCreateSeason() {
        SeasonModel newSeason = getNewSeason();
        entityManager.persist(newSeason);

        SeasonModel res = seasonRepository.save(newSeason);

        assertSeasonEquals(newSeason, res);
    }

    @Test
    public void shouldReturnSeasonByUUID() {
        Optional<SeasonModel> res = seasonRepository.findById(season.getUuid());

        assertTrue(res.isPresent());
        assertSeasonEquals(season, res.get());
    }

    @Test
    public void shouldReturnNotFoundSeason() {
        entityManager.clear();
        entityManager.flush();

        Optional<SeasonModel> res = seasonRepository.findById(season.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfSeasons() {
        List<SeasonModel> res = seasonRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfSeasons() {
        entityManager.clear();
        entityManager.flush();

        List<SeasonModel> res = seasonRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfSeasonsWithFilter() {
        SeasonFilter filter = new SeasonFilter();
        filter.setTitle("Lorem");

        SeasonModel convertedModel = modelMapper().map(filter, SeasonModel.class);
        Example<SeasonModel> exFilter = Example.of(convertedModel);
        List<SeasonModel> res = seasonRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertSeasonEquals(season, res.get(0));
    }

    @Test
    public void shouldUpdateSeason() {
        season.setTitle("New Lorem");
        SeasonModel updatedSeason = seasonRepository.save(season);

        assertEquals(season.getUuid(), updatedSeason.getUuid());
        assertEquals(season.getTitle(), updatedSeason.getTitle());

        season.setTitle("Lorem");
    }

    @Test
    public void shouldDeleteSeason() {
        seasonRepository.deleteById(season.getUuid());

        Optional<SeasonModel> opSeason = seasonRepository.findById(season.getUuid());

        assertFalse(opSeason.isPresent());
    }
}
