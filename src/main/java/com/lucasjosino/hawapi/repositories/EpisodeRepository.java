package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.EpisodeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EpisodeRepository extends JpaRepository<EpisodeModel, UUID> {
}
