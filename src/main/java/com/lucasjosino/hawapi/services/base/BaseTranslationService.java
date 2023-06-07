package com.lucasjosino.hawapi.services.base;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BaseTranslationService<D extends BaseDTO, T extends BaseTranslationDTO> extends BaseService<D> {

    List<T> findAllTranslationsBy(UUID uuid);

    T findRandomTranslation(UUID uuid);

    @Cacheable(value = "findBy")
    T findTranslationBy(UUID uuid, String language);

    @Transactional
    T saveTranslation(UUID uuid, T dto);

    @Transactional
    void patchTranslation(UUID uuid, String language, T patch) throws IOException;

    @Transactional
    void deleteTranslation(UUID uuid, String language);
}
