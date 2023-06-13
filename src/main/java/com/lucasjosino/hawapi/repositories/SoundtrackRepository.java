package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.repositories.base.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface that implements the Soundtrack Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see BaseJpaRepository
 * @see EntityGraph
 * @see SoundtrackModel
 * @since 1.0.0
 */
@Repository
public interface SoundtrackRepository extends BaseJpaRepository<SoundtrackModel, UUID> {
}
