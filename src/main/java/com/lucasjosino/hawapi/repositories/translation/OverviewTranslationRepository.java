package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.OverviewTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Overview Translation Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see JpaRepository
 * @see OverviewTranslation
 * @since 1.0.0
 */
public interface OverviewTranslationRepository extends JpaRepository<OverviewTranslation, UUID> {

    /**
     * Method to get a single overview translations by language.
     *
     * @param language An {@link String} that specify a language filter
     * @return An {@link Optional} of {@link OverviewTranslation}
     * @since 1.0.0
     */
    Optional<OverviewTranslation> findByLanguage(String language);

    /**
     * Method to check if an overview translation exists using its <strong>language</strong>.
     *
     * @param language An {@link String} that specify a language filter
     * @return true if translation exists
     * @since 1.0.0
     */
    boolean existsByLanguage(String language);

    /**
     * Method to delete an overview translation using its <strong>language</strong>.
     *
     * @param language An {@link String} that specify a language filter
     * @since 1.0.0
     */
    void deleteByLanguage(String language);
}
