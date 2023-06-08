package com.lucasjosino.hawapi.services.base;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BaseTranslationService<D extends BaseDTO, T extends BaseTranslationDTO> extends BaseService<D> {

    @Cacheable(value = "findAllTranslation")
    List<T> findAllTranslationsBy(UUID uuid);

    T findRandomTranslation(UUID uuid);

    @Cacheable(value = "findTranslationBy", key = "{ #uuid, #language }")
    T findTranslationBy(UUID uuid, String language);

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy"}, allEntries = true)
    T saveTranslation(UUID uuid, T dto);

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy"}, allEntries = true)
    void patchTranslation(UUID uuid, String language, T patch) throws IOException;

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy"}, allEntries = true)
    void deleteTranslation(UUID uuid, String language);
}
