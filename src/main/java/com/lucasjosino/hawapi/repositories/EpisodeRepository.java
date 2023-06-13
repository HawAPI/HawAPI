package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.EpisodeModel;
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
 * Interface that implements the Episode Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see BaseJpaRepository
 * @see EntityGraph
 * @see EpisodeModel
 * @since 1.0.0
 */
@Repository
@SuppressWarnings("NullableProblems")
public interface EpisodeRepository extends BaseJpaRepository<EpisodeModel, UUID> {

    @Override
    @EntityGraph(attributePaths = "translation")
    List<EpisodeModel> findAll();

    @Override
    @EntityGraph(attributePaths = "translation")
    List<EpisodeModel> findAllById(Iterable<UUID> ys);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<EpisodeModel> findAll(Specification<EpisodeModel> spec);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<EpisodeModel> findAll(Specification<EpisodeModel> spec, Sort sort);

    @Override
    @EntityGraph(attributePaths = "translation")
    Page<EpisodeModel> findAll(Specification<EpisodeModel> spec, Pageable pageable);

    /**
     * Method to get an episode by {@link UUID} and language.
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An {@link Optional} of {@link EpisodeModel}
     * @since 1.0.0
     */
    @EntityGraph(attributePaths = "translation")
    Optional<EpisodeModel> findByUuidAndTranslationLanguage(UUID uuid, String language);
}
