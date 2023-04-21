package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.LocationModel;
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
public interface LocationRepository extends BaseJpaRepository<LocationModel, UUID> {

    @Override
    @EntityGraph(attributePaths = "translation")
    List<LocationModel> findAll();

    @Override
    @EntityGraph(attributePaths = "translation")
    List<LocationModel> findAllById(Iterable<UUID> ys);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<LocationModel> findAll(Specification<LocationModel> spec);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<LocationModel> findAll(Specification<LocationModel> spec, Sort sort);

    @Override
    @EntityGraph(attributePaths = "translation")
    Page<LocationModel> findAll(Specification<LocationModel> spec, Pageable pageable);

    @EntityGraph(attributePaths = "translation")
    Optional<LocationModel> findByUuidAndTranslationLanguage(UUID uuid, String language);
}
