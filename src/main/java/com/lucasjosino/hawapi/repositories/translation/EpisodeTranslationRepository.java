package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Episode Translation Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see JpaRepository
 * @see EpisodeTranslation
 * @since 1.0.0
 */
@Repository
public interface EpisodeTranslationRepository extends JpaRepository<EpisodeTranslation, Integer> {

    /**
     * Method to get all episode translations by {@link UUID}.
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @return An {@link List} of {@link EpisodeTranslation}
     * @since 1.0.0
     */
    List<EpisodeTranslation> findAllByEpisodeUuid(UUID uuid);

    /**
     * Method to get all episode translations by {@link UUID} filtering with {@link PageRequest}.
     *
     * @param uuid     An {@link UUID} that represents a specific item
     * @param pageable An {@link PageRequest} with pageable params. Can be null
     * @return An {@link Page} of {@link EpisodeTranslation}
     * @since 1.0.0
     */
    Page<EpisodeTranslation> findAllByEpisodeUuid(UUID uuid, PageRequest pageable);

    /**
     * Method to get a single episode translations by {@link UUID} and language.
     *
     * @param episodeUuid An {@link UUID} that represents a specific item
     * @param language    An {@link String} that specify a language filter
     * @return An {@link Optional} of {@link EpisodeTranslation}
     * @since 1.0.0
     */
    Optional<EpisodeTranslation> findByEpisodeUuidAndLanguage(UUID episodeUuid, String language);

    /**
     * Method to check if an episode translation exists using its <strong>uuid</strong> and <strong>language</strong>.
     *
     * @param episodeUuid An {@link UUID} that represents a specific item
     * @param language    An {@link String} that specify a language filter
     * @return true if translation exists
     * @since 1.0.0
     */
    boolean existsByEpisodeUuidAndLanguage(UUID episodeUuid, String language);

    /**
     * Method to check if an episode translation exists using its <strong>title</strong> and <strong>language</strong>.
     *
     * @param title    An {@link String} that represents a specific item title
     * @param language An {@link String} that specify a language filter
     * @return true if translation exists
     * @since 1.0.0
     */
    boolean existsByTitleAndLanguage(String title, String language);

    /**
     * Method to delete an episode translation using its <strong>uuid</strong> and <strong>language</strong>.
     *
     * @param episodeUuid An {@link UUID} that represents a specific item title
     * @param language    An {@link String} that specify a language filter
     * @since 1.0.0
     */
    void deleteByEpisodeUuidAndLanguage(UUID episodeUuid, String language);
}