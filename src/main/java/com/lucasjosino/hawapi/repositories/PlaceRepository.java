package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.PlaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceModel, UUID> {
}
