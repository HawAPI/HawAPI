package com.lucasjosino.hawapi.services.base;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Base service interface that provides methods for manipulating translation objects.
 *
 * @param <D> param that extends {@link BaseDTO}
 * @param <T> param that extends {@link BaseTranslationDTO}
 * @author Lucas Josino
 * @see Transactional
 * @see Cacheable
 * @see Pageable
 * @see BaseService
 * @since 1.0.0
 */
public interface BaseTranslationService<D extends BaseDTO, T extends BaseTranslationDTO> extends BaseService<D> {

    @Override
    default List<D> findAll(Page<UUID> uuids) throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    @Cacheable(value = "findAll", keyGenerator = "findAllKeyGenerator")
    List<D> findAll(String language, Page<UUID> uuids);

    @Cacheable(value = "findAllTranslation")
    List<T> findAllTranslationsBy(UUID uuid);

    T findRandomTranslation(UUID uuid);

    @Cacheable(value = "findTranslationBy", key = "{ #uuid, #language }")
    T findTranslationBy(UUID uuid, String language);

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy", "findAll", "findBy"}, allEntries = true)
    T saveTranslation(UUID uuid, T dto);

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy", "findAll", "findBy"}, allEntries = true)
    void patchTranslation(UUID uuid, String language, T patch) throws IOException;

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy", "findAll", "findBy"}, allEntries = true)
    void deleteTranslation(UUID uuid, String language);
}
