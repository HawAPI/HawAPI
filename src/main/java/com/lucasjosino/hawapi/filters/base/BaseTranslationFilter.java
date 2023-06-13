package com.lucasjosino.hawapi.filters.base;

/**
 * A base translation filter model common fields.
 *
 * @author Lucas Josino
 * @since 1.0.0
 */
abstract public class BaseTranslationFilter extends BaseFilter {

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}