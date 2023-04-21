package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EpisodeTranslationRepository extends JpaRepository<EpisodeTranslation, Integer> {

    Optional<EpisodeTranslation> findByEpisodeUuidAndLanguage(UUID episodeUuid, String language);

    boolean existsByEpisodeUuidAndLanguage(UUID episodeUuid, String language);

    void deleteByEpisodeUuidAndLanguage(UUID episodeUuid, String language);
}