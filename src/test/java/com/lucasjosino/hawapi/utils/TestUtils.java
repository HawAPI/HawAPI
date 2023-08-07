package com.lucasjosino.hawapi.utils;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;

public class TestUtils {

    public static HttpHeaders buildHeaders(String language) {
        return new HttpHeaders() {{
            add("Content-Language", language);
        }};
    }

    public static HttpHeaders buildHeaders(Page<UUID> page, String language) {
        HttpHeaders headers = new HttpHeaders() {{
            // We add +1 because of 'one-indexed-parameters' is set to true
            add("X-Pagination-Page-Index", String.valueOf(page.getNumber() + 1));
            add("X-Pagination-Page-Size", String.valueOf(page.getNumberOfElements()));
            add("X-Pagination-Page-Total", String.valueOf(page.getTotalPages()));
            add("X-Pagination-Item-Total", String.valueOf(page.getTotalElements()));
        }};

        if (!isNullOrEmpty(language)) headers.add("Content-Language", language);

        return headers;
    }
}
