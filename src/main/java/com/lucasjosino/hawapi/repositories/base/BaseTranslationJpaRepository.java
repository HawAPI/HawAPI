package com.lucasjosino.hawapi.repositories.base;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface BaseTranslationJpaRepository<M extends BaseModel> {

    /**
     * Method to get all items and translation with defined language and sort
     *
     * @param language An {@link String} with filtering language
     * @param sort     Cannot be null
     * @return An {@link List} of {@link UUID}
     * @since 1.0.0
     */
    @EntityGraph(attributePaths = {"translation"})
    List<M> findAllByTranslationLanguageAndUuidIn(String language, Iterable<UUID> ys, Sort sort);
}
