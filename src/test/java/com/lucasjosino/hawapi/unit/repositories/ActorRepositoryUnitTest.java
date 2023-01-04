package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.PostgreSQLContainerConfig;
import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.TestsData.getNewActor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryUnitTestConfig
public class ActorRepositoryUnitTest extends PostgreSQLContainerConfig {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ActorRepository actorRepository;

    @Test
    public void shouldReturnSingleActorUsingUUID() {
        ActorModel actor = getNewActor();
        entityManager.persist(actor);

        Optional<ActorModel> res = actorRepository.findById(actor.getUuid());

        assertTrue(res.isPresent());
        assertEquals(actor.getUuid(), res.get().getUuid());
    }

    @Test
    public void shouldReturnEmptyActorList() {
        List<ActorModel> actors = actorRepository.findAll();

        assertTrue(actors.isEmpty());
    }
}
