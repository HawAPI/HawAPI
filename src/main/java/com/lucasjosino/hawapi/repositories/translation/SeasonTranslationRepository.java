package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeasonTranslationRepository extends JpaRepository<SeasonTranslation, Integer> {

    Optional<SeasonTranslation> findBySeasonUuidAndLanguage(UUID seasonUuid, String language);

    boolean existsBySeasonUuidAndLanguage(UUID seasonUuid, String language);

    void deleteBySeasonUuidAndLanguage(UUID seasonUuid, String language);
}