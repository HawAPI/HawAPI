package com.lucasjosino.hawapi.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class LanguageUtils {

    @Value("${com.lucasjosino.hawapi.application.default-language}")
    private String defaultLanguage;

    @Value("${com.lucasjosino.hawapi.application.languages}")
    private List<String> languages;

    public boolean isSupportedLanguage(String language) {
        for (String lang : languages) {
            if (lang.equalsIgnoreCase(language)) return true;
        }

        return false;
    }

    public boolean isDefaultLanguage(String language) {
        return language.equalsIgnoreCase(defaultLanguage.toLowerCase());
    }

    public String getDefaultLanguage() {
        if (StringUtils.isNullOrEmpty(defaultLanguage)) {
            throw new RuntimeException("Please specify the default language in 'application.properties'.");
        }

        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<String> getLanguages() {
        if (languages == null || languages.isEmpty()) {
            throw new RuntimeException("Please specify the supported languages in 'application.properties'.");
        }

        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}

