package com.lucasjosino.hawapi.filters.base;

abstract public class BaseTranslationFilter extends BaseFilter {

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}