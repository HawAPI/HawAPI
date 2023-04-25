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

    @EntityGraph(attributePaths = "translation")
    Optional<EpisodeModel> findByUuidAndTranslationLanguage(UUID uuid, String language);
}
