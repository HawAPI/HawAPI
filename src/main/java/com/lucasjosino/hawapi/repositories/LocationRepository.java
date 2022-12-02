package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.LocationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<LocationModel, UUID> {
}
