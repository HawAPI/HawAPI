package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.repositories.base.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EpisodeRepository extends BaseJpaRepository<EpisodeModel, UUID> {
}
