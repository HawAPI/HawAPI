package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.ActorModel;
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
public interface ActorRepository extends BaseJpaRepository<ActorModel, UUID> {

    @Override
    @EntityGraph(attributePaths = {"translation", "socials"})
    List<ActorModel> findAll();

    @Override
    @EntityGraph(attributePaths = {"translation", "socials"})
    List<ActorModel> findAllById(Iterable<UUID> ys);

    @Override
    @EntityGraph(attributePaths = {"translation", "socials"})
    List<ActorModel> findAll(Specification<ActorModel> spec);

    @Override
    @EntityGraph(attributePaths = {"translation", "socials"})
    List<ActorModel> findAll(Specification<ActorModel> spec, Sort sort);

    @Override
    @EntityGraph(attributePaths = {"translation", "socials"})
    Page<ActorModel> findAll(Specification<ActorModel> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"translation", "socials"})
    Optional<ActorModel> findByUuidAndTranslationLanguage(UUID uuid, String language);
}