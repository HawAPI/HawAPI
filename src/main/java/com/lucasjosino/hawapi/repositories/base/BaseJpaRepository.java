package com.lucasjosino.hawapi.repositories.base;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
@SuppressWarnings("NullableProblems")
public interface BaseJpaRepository<T extends BaseModel, Y> extends JpaRepository<T, Y>, JpaSpecificationExecutor<T> {

    @Query("SELECT uuid from #{#entityName}")
    List<UUID> findAllUUIDs(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<T> findAll();

    @Override
    @EntityGraph(attributePaths = "translation")
    List<T> findAllById(Iterable<Y> ys);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<T> findAll(Specification<T> spec);

    @Override
    @EntityGraph(attributePaths = "translation")
    List<T> findAll(Specification<T> spec, Sort sort);

    @Override
    @EntityGraph(attributePaths = "translation")
    Page<T> findAll(Specification<T> spec, Pageable pageable);

    @EntityGraph(attributePaths = "translation")
    Optional<T> findByUuidAndTranslationLanguage(UUID uuid, String language);
}