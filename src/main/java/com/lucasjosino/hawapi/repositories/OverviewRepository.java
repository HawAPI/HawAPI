package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.OverviewModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Overview Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see JpaRepository
 * @see EntityGraph
 * @see OverviewModel
 * @since 1.0.0
 */
@Repository
public interface OverviewRepository extends JpaRepository<OverviewModel, UUID> {

    /**
     * Method to get an overview by language.
     *
     * @param language An {@link String} that specify a language
     * @return An {@link Optional} of {@link OverviewModel}
     * @since 1.0.0
     */
    @EntityGraph(attributePaths = "translation")
    Optional<OverviewModel> findByTranslationLanguage(String language);
}
