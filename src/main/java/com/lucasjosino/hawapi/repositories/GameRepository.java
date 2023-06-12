package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.repositories.base.BaseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Game Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see BaseJpaRepository
 * @see EntityGraph
 * @see GameModel
 * @since 1.0.0
 */
@Repository
@SuppressWarnings("NullableProblems")
public interface GameRepository extends BaseJpaRepository<GameModel, UUID> {

    @Override
    @EntityGraph(attributePaths = "translation")
    List<GameModel> findAll();

    @Override
    @EntityGraph(attributePaths = "translation")
    List<GameModel> findAllById(Iterable<UUID> ys);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<GameModel> findAll(Specification<GameModel> spec);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<GameModel> findAll(Specification<GameModel> spec, Sort sort);

    @Override
    @EntityGraph(attributePaths = "translation")
    Page<GameModel> findAll(Specification<GameModel> spec, Pageable pageable);

    /**
     * Method to get a game by {@link UUID} and language.
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An {@link Optional} of {@link GameModel}
     * @since 1.0.0
     */
    @EntityGraph(attributePaths = "translation")
    Optional<GameModel> findByUuidAndTranslationLanguage(UUID uuid, String language);
}
