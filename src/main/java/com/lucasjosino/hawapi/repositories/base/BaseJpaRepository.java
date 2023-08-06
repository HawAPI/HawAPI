package com.lucasjosino.hawapi.repositories.base;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * A base JPA Repository interface with all common methods.
 *
 * @param <T>  An object that extends {@link BaseModel}
 * @param <ID> An object that represents the entity id
 * @author Lucas Josino
 * @see JpaRepository
 * @see BaseRepository
 * @see JpaSpecificationExecutor
 * @since 1.0.0
 */
@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseModel, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>, BaseRepository<T, ID> {

    /**
     * Method to get all items in list by sort
     *
     * @param ys   An {@link Iterable} of {@link ID}. Cannot be empty
     * @param sort An {@link Sort}. Cannot be null
     * @return An {@link List} of {@link T} or empty
     * @since 1.0.0
     */
    List<T> findAllByUuidIn(Iterable<ID> ys, Sort sort);
}