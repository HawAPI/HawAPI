package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.repositories.base.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocationRepository extends BaseJpaRepository<LocationModel, UUID> {
}
