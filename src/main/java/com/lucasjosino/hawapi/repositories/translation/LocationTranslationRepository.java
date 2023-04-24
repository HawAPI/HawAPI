package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationTranslationRepository extends JpaRepository<LocationTranslation, Integer> {

    List<EpisodeTranslation> findAllByLocationUuid(UUID uuid);

    Optional<LocationTranslation> findByLocationUuidAndLanguage(UUID locationUuid, String language);

    boolean existsByLocationUuidAndLanguage(UUID locationUuid, String language);

    boolean existsByNameAndLanguage(String title, String language);

    void deleteByLocationUuidAndLanguage(UUID locationUuid, String language);
}