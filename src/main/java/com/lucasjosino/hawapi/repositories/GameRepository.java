package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.repositories.base.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends BaseJpaRepository<GameModel, UUID> {
}
