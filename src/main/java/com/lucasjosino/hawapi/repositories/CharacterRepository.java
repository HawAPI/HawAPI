package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.CharacterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<CharacterModel, UUID> {
}
