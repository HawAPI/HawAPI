package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.SeasonModel;
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
public interface SeasonRepository extends BaseJpaRepository<SeasonModel, UUID> {

    @Override
    @EntityGraph(attributePaths = "translation")
    List<SeasonModel> findAll();

    @Override
    @EntityGraph(attributePaths = "translation")
    List<SeasonModel> findAllById(Iterable<UUID> ys);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<SeasonModel> findAll(Specification<SeasonModel> spec);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<SeasonModel> findAll(Specification<SeasonModel> spec, Sort sort);

    @Override
    @EntityGraph(attributePaths = "translation")
    Page<SeasonModel> findAll(Specification<SeasonModel> spec, Pageable pageable);

    @EntityGraph(attributePaths = "translation")
    Optional<SeasonModel> findByUuidAndTranslationLanguage(UUID uuid, String language);
}
