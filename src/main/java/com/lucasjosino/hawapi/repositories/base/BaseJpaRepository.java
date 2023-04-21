package com.lucasjosino.hawapi.repositories.base;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseModel, Y> extends JpaRepository<T, Y>, JpaSpecificationExecutor<T> {

    @Query("SELECT uuid from #{#entityName}")
    List<UUID> findAllUUIDs(Pageable pageable);
}