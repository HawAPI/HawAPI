package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.OverviewModel;
import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Method to get first {@link UUID} value from {@link OverviewModel}
     * <p>
     * OBS: Native Query
     *
     * @return A single and unique {@link OverviewModel} {@link UUID}
     * @since 1.0.0
     */
    @Query(value = "SELECT CAST(uuid AS VARCHAR) FROM overviews LIMIT 1", nativeQuery = true)
    Optional<String> findUUID();

    /**
     * Method to get the item count from all repositories
     * <p>
     * OBS: Native Query
     *
     * @return A {@link OverviewDTO.DataCountProjection} with a count of all repositories
     * @since 1.2.0
     */
    @Query(value = "SELECT " +
            "(SELECT count(*) from actors) as actors, " +
            "(SELECT count(*) from characters) as characters, " +
            "(SELECT count(*) from episodes) as episodes, " +
            "(SELECT count(*) from games) as games, " +
            "(SELECT count(*) from locations) as locations, " +
            "(SELECT count(*) from seasons) as seasons, " +
            "(SELECT count(*) from soundtracks) as soundtracks",
            nativeQuery = true
    )
    OverviewDTO.DataCountProjection getAllCounts();
}
