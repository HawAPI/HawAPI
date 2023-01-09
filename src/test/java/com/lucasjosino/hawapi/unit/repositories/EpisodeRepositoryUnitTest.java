package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertEpisodeEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class EpisodeRepositoryUnitTest extends DatabaseContainerInitializer {

    private static final EpisodeModel episode = getSingleEpisode();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EpisodeRepository episodeRepository;

    @BeforeEach
    public void setUp() {
        getEpisodes().forEach(entityManager::persist);
    }

    @Test
    public void shouldCreateEpisode() {
        EpisodeModel newEpisode = getNewEpisode();
        entityManager.persist(newEpisode);

        EpisodeModel res = episodeRepository.save(newEpisode);

        assertEpisodeEquals(newEpisode, res);
    }

    @Test
    public void shouldReturnEpisodeByUUID() {
        Optional<EpisodeModel> res = episodeRepository.findById(episode.getUuid());

        assertTrue(res.isPresent());
        assertEpisodeEquals(episode, res.get());
    }

    @Test
    public void shouldReturnNotFoundEpisode() {
        entityManager.clear();
        entityManager.flush();

        Optional<EpisodeModel> res = episodeRepository.findById(episode.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfEpisodes() {
        List<EpisodeModel> res = episodeRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfEpisodes() {
        entityManager.clear();
        entityManager.flush();

        List<EpisodeModel> res = episodeRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfEpisodesWithFilter() {
        ModelMapper mapper = new ModelMapper();
        
        episode.setTitle("Lorem");

        EpisodeFilter filter = new EpisodeFilter();
        filter.setTitle("Lorem");

        EpisodeModel convertedModel = mapper.map(filter, EpisodeModel.class);
        Example<EpisodeModel> exFilter = Example.of(convertedModel);
        List<EpisodeModel> res = episodeRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertEpisodeEquals(episode, res.get(0));
    }

    @Test
    public void shouldUpdateEpisode() {
        episode.setTitle("New Lorem");
        EpisodeModel updatedEpisode = episodeRepository.save(episode);

        assertEquals(episode.getUuid(), updatedEpisode.getUuid());
        assertEquals(episode.getTitle(), updatedEpisode.getTitle());
    }

    @Test
    public void shouldDeleteEpisode() {
        episodeRepository.deleteById(episode.getUuid());

        Optional<EpisodeModel> opEpisode = episodeRepository.findById(episode.getUuid());

        assertFalse(opEpisode.isPresent());
    }
}
