package com.lucasjosino.hawapi.controllers.utils;

import com.lucasjosino.hawapi.core.LanguageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.lucasjosino.hawapi.core.StringUtils.isNullOrEmpty;


/**
 * A utils class for controllers
 *
 * @author Lucas Josino
 * @since 1.0.0
 */
@Component
public class ResponseUtils {

    private static final Sort defaultSort = Sort.by("uuid").ascending();

    private final LanguageUtils languageUtils;

    @Autowired
    public ResponseUtils(LanguageUtils languageUtils) {
        this.languageUtils = languageUtils;
    }

    /**
     * Method to build an {@link HttpHeaders} with:
     * <ul>
     *     <li>X-Pagination-Page-Index</li>
     *     <li>X-Pagination-Page-Size</li>
     *     <li>X-Pagination-Page-Total</li>
     *     <li>X-Pagination-Item-Total</li>
     *     <li>Content-Language</li>
     * </ul>
     *
     * @param page     An {@link Page} of {@link UUID}. Cannot be null
     * @param language An {@link String} with language. Can be null
     * @return An {@link HttpHeaders} with all defined params
     * @since 1.0.0
     */
    public HttpHeaders getHeaders(Page<UUID> page, String language) {
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

    /**
     * Method to build an {@link HttpHeaders} with:
     * <ul>
     *     <li>Content-Language</li>
     * </ul>
     *
     * @param language An {@link String} with language. Can be null
     * @return An {@link HttpHeaders} with all defined params
     * @since 1.0.0
     */
    public HttpHeaders getHeaders(String language) {
        return new HttpHeaders() {{
            add("Content-Language", language);
        }};
    }

    /**
     * Validate the {@link Sort} value. Will set the default sort if is unsorted.
     *
     * @param pageable The {@link Pageable} variable containing the {@link Sort} to be validated
     * @return A new {@link Pageable} with default {@link Sort} if is unsorted
     * @since 1.0.0
     */
    public Pageable validateSort(Pageable pageable) {
        if (pageable.getSort().isSorted()) return pageable;

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }

    public String getDefaultLanguage() {
        return languageUtils.getDefaultLanguage();
    }
}