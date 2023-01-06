package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.PostgreSQLContainerInitializer;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.TestsData.getActors;
import static com.lucasjosino.hawapi.utils.TestsData.getNewActor;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class ActorRepositoryUnitTest extends PostgreSQLContainerInitializer {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ActorRepository actorRepository;

    @Test
    public void shouldCreateActor() {
        ActorModel newActor = getNewActor();
        entityManager.persist(newActor);

        ActorModel res = actorRepository.save(newActor);

        assertEquals(newActor.getUuid(), res.getUuid());
        assertEquals(newActor.getHref(), res.getHref());
        assertEquals(newActor.getFirstName(), res.getFirstName());
        assertEquals(newActor.getLastName(), res.getLastName());
        assertEquals(newActor.getGender(), res.getGender());
        assertEquals(newActor.getCharacter(), res.getCharacter());
    }

    @Test
    public void shouldReturnActorByUUID() {
        ActorModel newActor = getNewActor();
        entityManager.persist(newActor);

        Optional<ActorModel> res = actorRepository.findById(newActor.getUuid());

        assertTrue(res.isPresent());
        assertEquals(newActor.getUuid(), res.get().getUuid());
        assertEquals(newActor.getHref(), res.get().getHref());
        assertEquals(newActor.getFirstName(), res.get().getFirstName());
        assertEquals(newActor.getLastName(), res.get().getLastName());
        assertEquals(newActor.getGender(), res.get().getGender());
        assertEquals(newActor.getCharacter(), res.get().getCharacter());
    }

    @Test
    public void shouldReturnNotFoundActor() {
        ActorModel newActor = getNewActor();

        Optional<ActorModel> res = actorRepository.findById(newActor.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfActors() {
        getActors().forEach(entityManager::persist);

        List<ActorModel> res = actorRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfActors() {
        List<ActorModel> res = actorRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfActorsWithFilter() {
        ModelMapper mapper = new ModelMapper();

        ActorModel filteredActor = getActors().get(0);
        getActors().forEach(entityManager::persist);

        ActorFilter filter = new ActorFilter();
        filter.setFirstName("John");

        ActorModel convertedModel = mapper.map(filter, ActorModel.class);
        Example<ActorModel> exFilter = Example.of(convertedModel);
        List<ActorModel> res = actorRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertEquals(filteredActor.getUuid(), res.get(0).getUuid());
        assertEquals(filteredActor.getHref(), res.get(0).getHref());
        assertEquals(filteredActor.getFirstName(), res.get(0).getFirstName());
        assertEquals(filteredActor.getLastName(), res.get(0).getLastName());
        assertEquals(filteredActor.getGender(), res.get(0).getGender());
        assertEquals(filteredActor.getCharacter(), res.get(0).getCharacter());
    }

    @Test
    public void shouldUpdateActor() {
        ActorModel actor = getNewActor();
        entityManager.persist(actor);

        actor.setFirstName("Mario");
        ActorModel updatedActor = actorRepository.save(actor);

        assertEquals(actor.getUuid(), updatedActor.getUuid());
        assertEquals(actor.getFirstName(), updatedActor.getFirstName());
    }

    @Test
    public void shouldDeleteActor() {
        ActorModel actor = getNewActor();
        entityManager.persist(actor);

        actorRepository.deleteById(actor.getUuid());

        Optional<ActorModel> opActor = actorRepository.findById(actor.getUuid());

        assertFalse(opActor.isPresent());
    }
}
