package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import com.lucasjosino.hawapi.models.dto.translation.OverviewTranslationDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface OverviewService {

    @Cacheable(value = "findAllTranslation", key = "{ #root.methodName }")
    List<OverviewTranslationDTO> findAllOverviewTranslations();

    @Cacheable(value = "findBy", key = "{ #root.methodName, #language }")
    OverviewDTO findOverviewBy(String language);

    @Cacheable(value = "findTranslationBy", key = "{ #root.methodName, #language }")
    OverviewTranslationDTO findOverviewTranslationBy(String language);

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, allEntries = true)
    OverviewDTO saveOverview(OverviewDTO dto);

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy", "findAll", "findBy"}, allEntries = true)
    OverviewTranslationDTO saveOverviewTranslation(String defaultLanguage, OverviewTranslationDTO dto);

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, allEntries = true)
    void patchOverview(OverviewDTO patch) throws IOException;

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy", "findAll", "findBy"}, allEntries = true)
    void patchOverviewTranslation(String language, OverviewTranslationDTO patch) throws IOException;

    @Transactional
    @CacheEvict(cacheNames = {"findAll", "findBy"}, allEntries = true)
    void deleteOverview();

    @Transactional
    @CacheEvict(cacheNames = {"findAllTranslation", "findTranslationBy", "findAll", "findBy"}, allEntries = true)
    void deleteOverviewTranslation(String language);
}
