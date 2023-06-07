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

public interface BaseService<D extends BaseDTO> {

    Page<UUID> findAllUUIDs(Pageable pageable);

    @Cacheable(value = "findAll", keyGenerator = "findAllKeyGenerator")
    List<D> findAll(Map<String, String> filters, List<UUID> uuids);

    D findRandom(String language);

    @Cacheable(value = "findBy", key = "#uuid")
    D findBy(UUID uuid, String language);

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, key = "#dto.uuid")
    D save(D dto);

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, key = "#uuid")
    void patch(UUID uuid, D patch) throws IOException;

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, key = "#uuid")
    void deleteById(UUID uuid);
}
