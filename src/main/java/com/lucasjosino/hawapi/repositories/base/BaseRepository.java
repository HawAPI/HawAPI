package com.lucasjosino.hawapi.repositories.base;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<M extends BaseModel, ID> {

    /**
     * Method to get all items with filters and {@link Pageable}
     *
     * @param specification An {@link Specification} with all filter params. Can be empty
     * @param pageable      Cannot be null
     * @return An {@link List} of {@link UUID}
     * @since 1.0.0
     */
    Page<UUID> findAllUUIDs(Specification<M> specification, Pageable pageable);
}
