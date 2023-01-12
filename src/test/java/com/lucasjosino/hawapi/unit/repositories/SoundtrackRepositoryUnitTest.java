package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.filters.SoundtrackFilter;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertSoundtrackEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.modelMapper;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class SoundtrackRepositoryUnitTest extends DatabaseContainerInitializer {

    private static final SoundtrackModel soundtrack = getSingleSoundtrack();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SoundtrackRepository soundtrackRepository;

    @BeforeEach
    public void setUp() {
        entityManager.clear();
        entityManager.flush();
        soundtrackRepository.deleteAll();
        getSoundtracks().forEach(entityManager::persist);
    }

    @AfterAll
    public void cleanUp() {
        soundtrackRepository.deleteAll();
    }

    @Test
    public void shouldCreateSoundtrack() {
        SoundtrackModel newSoundtrack = getNewSoundtrack();
        entityManager.persist(newSoundtrack);

        SoundtrackModel res = soundtrackRepository.save(newSoundtrack);

        assertSoundtrackEquals(newSoundtrack, res);
    }

    @Test
    public void shouldReturnSoundtrackByUUID() {
        Optional<SoundtrackModel> res = soundtrackRepository.findById(soundtrack.getUuid());

        assertTrue(res.isPresent());
        assertSoundtrackEquals(soundtrack, res.get());
    }

    @Test
    public void shouldReturnNotFoundSoundtrack() {
        entityManager.clear();
        entityManager.flush();

        Optional<SoundtrackModel> res = soundtrackRepository.findById(soundtrack.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfSoundtracks() {
        List<SoundtrackModel> res = soundtrackRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfSoundtracks() {
        entityManager.clear();
        entityManager.flush();

        List<SoundtrackModel> res = soundtrackRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfSoundtracksWithFilter() {
        SoundtrackFilter filter = new SoundtrackFilter();
        filter.setName("Lorem");

        SoundtrackModel convertedModel = modelMapper().map(filter, SoundtrackModel.class);
        Example<SoundtrackModel> exFilter = Example.of(convertedModel);
        List<SoundtrackModel> res = soundtrackRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertSoundtrackEquals(soundtrack, res.get(0));
    }

    @Test
    public void shouldUpdateSoundtrack() {
        soundtrack.setName("New Lorem");
        SoundtrackModel updatedSoundtrack = soundtrackRepository.save(soundtrack);

        assertEquals(soundtrack.getUuid(), updatedSoundtrack.getUuid());
        assertEquals(soundtrack.getName(), updatedSoundtrack.getName());

        soundtrack.setName("Lorem");
    }

    @Test
    public void shouldDeleteSoundtrack() {
        soundtrackRepository.deleteById(soundtrack.getUuid());

        Optional<SoundtrackModel> opSoundtrack = soundtrackRepository.findById(soundtrack.getUuid());

        assertFalse(opSoundtrack.isPresent());
    }
}
