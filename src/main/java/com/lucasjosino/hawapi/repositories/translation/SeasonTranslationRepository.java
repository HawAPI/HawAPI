package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Season Translation Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see JpaRepository
 * @see SeasonTranslation
 * @since 1.0.0
 */
@Repository
public interface SeasonTranslationRepository extends JpaRepository<SeasonTranslation, Integer> {

    /**
     * Method to get all season translations by {@link UUID}.
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An {@link List} of {@link SeasonTranslation}
     * @since 1.0.0
     */
    List<SeasonTranslation> findAllBySeasonUuid(UUID uuid);

    /**
     * Method to get all season translations by {@link UUID} filtering with {@link PageRequest}.
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param pageable An {@link PageRequest} with pageable params. Can be null
     * @return An {@link Page} of {@link SeasonTranslation}
     * @since 1.0.0
     */
    Page<SeasonTranslation> findAllBySeasonUuid(UUID uuid, PageRequest pageable);

    /**
     * Method to get a single season translations by {@link UUID} and language.
     *
     * @param seasonUuid An {@link UUID} that represents a specific item
     * @param language   An {@link String} that specify a language filter
     * @return An {@link Optional} of {@link SeasonTranslation}
     * @since 1.0.0
     */
    Optional<SeasonTranslation> findBySeasonUuidAndLanguage(UUID seasonUuid, String language);

    /**
     * Method to check if a season translation exists using its <strong>uuid</strong> and <strong>language</strong>.
     *
     * @param seasonUuid An {@link UUID} that represents a specific item
     * @param language   An {@link String} that specify a language filter
     * @return true if translation exists
     * @since 1.0.0
     */
    boolean existsBySeasonUuidAndLanguage(UUID seasonUuid, String language);

    /**
     * Method to check if a season translation exists using its <strong>title</strong> and <strong>language</strong>.
     *
     * @param title    An {@link String} that represents a specific item title
     * @param language An {@link String} that specify a language filter
     * @return true if translation exists
     * @since 1.0.0
     */
    boolean existsByTitleAndLanguage(String title, String language);

    /**
     * Method to delete a season translation using its <strong>uuid</strong> and <strong>language</strong>.
     *
     * @param seasonUuid An {@link UUID} that represents a specific item title
     * @param language   An {@link String} that specify a language filter
     * @since 1.0.0
     */
    void deleteBySeasonUuidAndLanguage(UUID seasonUuid, String language);
}