package com.lucasjosino.hawapi.controllers.utils;

import com.lucasjosino.hawapi.core.LanguageUtils;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;


@Component
public class ResponseUtils {

    private final LanguageUtils languageUtils;

    @Autowired
    public ResponseUtils(LanguageUtils languageUtils) {
        this.languageUtils = languageUtils;
    }

    public <T extends BaseModel> HttpHeaders getHeaders(
            Page<UUID> modelPage,
            Pageable pageable,
            String language,
            long totalCount
    ) {
        HttpHeaders headers = new HttpHeaders() {{
            add("X-Pagination-Page-Index", String.valueOf(pageable.getPageNumber() + 1));
            add("X-Pagination-Page-Size", String.valueOf(pageable.getPageSize()));
            add("X-Pagination-Page-Total", String.valueOf(modelPage.getTotalPages()));
            add("X-Pagination-Item-Total", String.valueOf(totalCount));
        }};

        if (!isNullOrEmpty(language)) headers.add("Content-Language", language);

        return headers;
    }

    public HttpHeaders getHeaders(String language) {
        return new HttpHeaders() {{
            add("Content-Language", language);
        }};
    }

    public String getDefaultLanguage() {
        return languageUtils.getDefaultLanguage();
    }
}