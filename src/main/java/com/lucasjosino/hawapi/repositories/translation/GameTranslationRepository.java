package com.lucasjosino.hawapi.repositories.translation;

import com.lucasjosino.hawapi.models.translations.GameTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameTranslationRepository extends JpaRepository<GameTranslation, Integer> {

    List<GameTranslation> findAllByGameUuid(UUID uuid);

    Optional<GameTranslation> findByGameUuidAndLanguage(UUID gameUuid, String language);

    boolean existsByGameUuidAndLanguage(UUID gameUuid, String language);

    boolean existsByNameAndLanguage(String title, String language);

    void deleteByGameUuidAndLanguage(UUID gameUuid, String language);
}