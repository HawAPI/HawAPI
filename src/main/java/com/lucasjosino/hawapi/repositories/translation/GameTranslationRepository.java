package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.GameTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Game Translation Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see JpaRepository
 * @see GameTranslation
 * @since 1.0.0
 */
@Repository
public interface GameTranslationRepository extends JpaRepository<GameTranslation, Integer> {

    /**
     * Method to get all game translations by {@link UUID}.
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An {@link List} of {@link GameTranslation}
     * @since 1.0.0
     */
    List<GameTranslation> findAllByGameUuid(UUID uuid);

    /**
     * Method to get all game translations by {@link UUID} filtering with {@link PageRequest}.
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param pageable An {@link PageRequest} with pageable params. Can be null
     * @return An {@link Page} of {@link GameTranslation}
     * @since 1.0.0
     */
    Page<GameTranslation> findAllByGameUuid(UUID uuid, PageRequest pageable);

    /**
     * Method to get a single game translations by {@link UUID} and language.
     *
     * @param gameUuid An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return An {@link Optional} of {@link GameTranslation}
     * @since 1.0.0
     */
    Optional<GameTranslation> findByGameUuidAndLanguage(UUID gameUuid, String language);

    /**
     * Method to check if a game translation exists using its <strong>uuid</strong> and <strong>language</strong>.
     *
     * @param gameUuid An {@link UUID} that represents a specific item
     * @param language An {@link String} that specify a language filter
     * @return true if translation exists
     * @since 1.0.0
     */
    boolean existsByGameUuidAndLanguage(UUID gameUuid, String language);

    /**
     * Method to check if a game translation exists using its <strong>title</strong> and <strong>language</strong>.
     *
     * @param title    An {@link String} that represents a specific item title
     * @param language An {@link String} that specify a language filter
     * @return true if translation exists
     * @since 1.0.0
     */
    boolean existsByNameAndLanguage(String title, String language);

    /**
     * Method to delete a game translation using its <strong>uuid</strong> and <strong>language</strong>.
     *
     * @param gameUuid An {@link UUID} that represents a specific item title
     * @param language An {@link String} that specify a language filter
     * @since 1.0.0
     */
    void deleteByGameUuidAndLanguage(UUID gameUuid, String language);
}