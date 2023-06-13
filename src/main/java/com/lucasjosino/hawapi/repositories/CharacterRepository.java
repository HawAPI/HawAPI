package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.repositories.base.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface that implements the Character Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see BaseJpaRepository
 * @see CharacterModel
 * @since 1.0.0
 */
@Repository
public interface CharacterRepository extends BaseJpaRepository<CharacterModel, UUID> {
}
