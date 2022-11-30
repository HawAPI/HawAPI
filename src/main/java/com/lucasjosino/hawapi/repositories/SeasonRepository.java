package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.SeasonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeasonRepository extends JpaRepository<SeasonModel, UUID> {
}
