package com.lucasjosino.hawapi.services.base;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Base service interface that provides methods for manipulating objects.
 *
 * @param <D> param that extends {@link BaseDTO}
 * @author Lucas Josino
 * @see Transactional
 * @see Cacheable
 * @see Pageable
 * @since 1.0.0
 */
public interface BaseService<D extends BaseDTO> {

    @Cacheable(value = "findAll", key = "{ #root.targetClass, #root.methodName, #p0, #p1 }")
    Page<UUID> findAllUUIDs(Map<String, String> filters, Pageable pageable);

    @Cacheable(value = "findAll", key = "{ #root.targetClass, #root.methodName, #p0.getPageable() }")
    List<D> findAll(Page<UUID> uuids) throws NoSuchMethodException;

    D findRandom(String language);

    @Cacheable(value = "findBy", key = "{ #p0, #p1 }")
    D findBy(UUID uuid, String language);

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, allEntries = true)
    D save(D dto);

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, allEntries = true)
    void patch(UUID uuid, D patch) throws IOException;

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, allEntries = true)
    void deleteById(UUID uuid);
}
