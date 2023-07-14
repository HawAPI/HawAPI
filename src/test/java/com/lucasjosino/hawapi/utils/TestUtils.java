package com.lucasjosino.hawapi.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;

public class TestUtils {

    public static HttpHeaders buildHeaders(Pageable pageable, Page<UUID> uuids, String language) {
        HttpHeaders headers = new HttpHeaders() {{
            add("X-Pagination-Page-Index", String.valueOf(pageable.getPageNumber() + 1));
            add("X-Pagination-Page-Size", String.valueOf(pageable.getPageSize()));
            add("X-Pagination-Page-Total", String.valueOf(uuids.getTotalPages()));
            add("X-Pagination-Item-Total", String.valueOf(uuids.getContent().size()));
        }};

        if (!isNullOrEmpty(language)) headers.add("Content-Language", language);

        return headers;
    }

    public static HttpHeaders buildHeaders(String language) {
        return new HttpHeaders() {{
            add("Content-Language", language);
        }};
    }
}
