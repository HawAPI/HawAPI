package com.lucasjosino.hawapi.repositories;

import com.lucasjosino.hawapi.models.ActorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<ActorModel, Integer> {
}
