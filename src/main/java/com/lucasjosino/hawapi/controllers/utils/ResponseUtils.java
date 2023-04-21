package com.lucasjosino.hawapi.controllers.utils;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResponseUtils {

    @Value("${com.lucasjosino.hawapi.default.language}")
    private String defaultLanguage;

    public <T extends BaseModel> HttpHeaders getHeaders(
            String language,
            Page<UUID> modelPage,
            Pageable pageable,
            int count
    ) {
        String languageOrDefault = language != null ? language : defaultLanguage;
        return new HttpHeaders() {{
            add("X-Pagination-Page", String.valueOf(pageable.getPageNumber() + 1));
            add("X-Pagination-Page-Size", String.valueOf(pageable.getPageSize()));
            add("X-Pagination-Page-Count", String.valueOf(modelPage.getTotalPages()));
            add("X-Pagination-Item-Count", String.valueOf(count));
            add("Content-Language", languageOrDefault);
        }};
    }

    public HttpHeaders getHeaders(String language) {
        return new HttpHeaders() {{
            add("Content-Language", language);
        }};
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }
}