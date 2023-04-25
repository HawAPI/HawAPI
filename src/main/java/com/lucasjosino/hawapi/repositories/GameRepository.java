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

    @EntityGraph(attributePaths = "translation")
    Optional<GameModel> findByUuidAndTranslationLanguage(UUID uuid, String language);
}
