package com.lucasjosino.hawapi.repositories.base;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

/**
 * A base JPA Repository interface with all common methods.
 *
 * @param <T> An object that extends {@link BaseModel}
 * @param <Y> An object that represents the entity id
 * @author Lucas Josino
 * @see JpaSpecificationExecutor
 * @see JpaRepository
 * @since 1.0.0
 */
@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseModel, Y> extends JpaRepository<T, Y>, JpaSpecificationExecutor<T> {

    /**
     * Method to get all items filtering with {@link Pageable}
     *
     * @param pageable Cannot be null
     * @return An {@link List} of {@link UUID}
     * @since 1.0.0
     */
    @Query("SELECT uuid from #{#entityName}")
    List<UUID> findAllUUIDs(Pageable pageable);
}