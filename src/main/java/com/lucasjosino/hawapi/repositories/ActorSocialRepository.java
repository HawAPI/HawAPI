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

    /**
     * Method to get a single actor social
     *
     * @param actorUuid An {@link UUID} that represents a specific item
     * @param social    An {@link String} that specify an actor social
     * @return An {@link Optional} of {@link ActorSocialModel}
     * @since 1.0.0
     */
    Optional<ActorSocialModel> findByActorUuidAndSocial(UUID actorUuid, String social);

    /**
     * Method to check if an actor social exists
     *
     * @param actorUuid An {@link UUID} that represents a specific item
     * @param social    An {@link String} that specify an actor social
     * @return true if social exists
     * @since 1.0.0
     */
    boolean existsByActorUuidAndSocial(UUID actorUuid, String social);

    /**
     * Method to delete an actor social
     *
     * @param actorUuid An {@link UUID} that represents a specific item title
     * @param social    An {@link String} that specify an actor social
     * @since 1.0.0
     */
    void deleteByActorUuidAndSocial(UUID actorUuid, String social);
}