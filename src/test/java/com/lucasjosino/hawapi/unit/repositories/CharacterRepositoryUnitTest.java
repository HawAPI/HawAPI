package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertActorEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class ActorRepositoryUnitTest extends DatabaseContainerInitializer {

    private static final ActorModel actor = getSingleActor();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ActorRepository actorRepository;

    @BeforeEach
    public void setUp() {
        getActors().forEach(entityManager::persist);
    }

    @Test
    public void shouldCreateActor() {
        ActorModel newActor = getNewActor();
        entityManager.persist(newActor);

        ActorModel res = actorRepository.save(newActor);

        assertActorEquals(newActor, res);
    }

    @Test
    public void shouldReturnActorByUUID() {
        Optional<ActorModel> res = actorRepository.findById(actor.getUuid());

        assertTrue(res.isPresent());
        assertActorEquals(actor, res.get());
    }

    @Test
    public void shouldReturnNotFoundActor() {
        entityManager.clear();
        entityManager.flush();

        Optional<ActorModel> res = actorRepository.findById(actor.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfActors() {
        List<ActorModel> res = actorRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfActors() {
        entityManager.clear();
        entityManager.flush();

        List<ActorModel> res = actorRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfActorsWithFilter() {
        ModelMapper mapper = new ModelMapper();

        ActorFilter filter = new ActorFilter();
        filter.setFirstName("John");

        ActorModel convertedModel = mapper.map(filter, ActorModel.class);
        Example<ActorModel> exFilter = Example.of(convertedModel);
        List<ActorModel> res = actorRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertActorEquals(actor, res.get(0));
    }

    @Test
    public void shouldUpdateActor() {
        actor.setFirstName("Mario");
        ActorModel updatedActor = actorRepository.save(actor);

        assertEquals(actor.getUuid(), updatedActor.getUuid());
        assertEquals(actor.getFirstName(), updatedActor.getFirstName());
    }

    @Test
    public void shouldDeleteActor() {
        actorRepository.deleteById(actor.getUuid());

        Optional<ActorModel> opActor = actorRepository.findById(actor.getUuid());

        assertFalse(opActor.isPresent());
    }
}
