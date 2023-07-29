package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import org.springframework.cache.annotation.Cacheable;

public interface OverviewService {

    @Cacheable(value = "findTranslationBy", key = "{ #root.methodName, #language }")
    OverviewDTO getOverviewByLanguage(String language);
}
