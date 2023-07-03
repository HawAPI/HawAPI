package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.ActorSocialModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the (actor) Social Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see JpaRepository
 * @see EntityGraph
 * @see ActorSocialModel
 * @since 1.0.0
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
@Repository
public interface ActorSocialRepository extends JpaRepository<ActorSocialModel, Integer> {

    Optional<ActorSocialModel> findByActorUuidAndSocial(UUID actorUuid, String social);

    boolean existsByActorUuidAndSocial(UUID actorUuid, String social);

    void deleteByActorUuidAndSocial(UUID actorUuid, String social);
}