package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.repositories.base.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SoundtrackRepository extends BaseJpaRepository<SoundtrackModel, UUID> {
}
