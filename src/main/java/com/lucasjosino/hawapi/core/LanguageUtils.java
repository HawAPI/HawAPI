package com.lucasjosino.hawapi.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A language utils for HawAPI project
 *
 * @author Lucas Josino
 * @since 1.0.0
 */
@Component
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class LanguageUtils {

    @Value("${com.lucasjosino.hawapi.application.default-language}")
    private String defaultLanguage;

    @Value("${com.lucasjosino.hawapi.application.languages}")
    private List<String> languages;

    /**
     * Checks if param <strong>language</strong> is a valid
     *
     * @param language Cannot be null or empty
     * @return true if is supported
     */
    public boolean isSupportedLanguage(String language) {
        for (String lang : languages) {
            if (lang.equalsIgnoreCase(language)) return true;
        }

        return false;
    }

    /**
     * Checks if param <strong>language</strong> is the default language
     *
     * @param language Cannot be null or empty
     * @return true if is default
     */
    public boolean isDefaultLanguage(String language) {
        return language.equalsIgnoreCase(defaultLanguage.toLowerCase());
    }

    /**
     * Method to get the default language of the HawAPI project.
     * <p> This value is defined in the application.properties using:
     * <pre>
     * com.lucasjosino.hawapi.application.default-language
     * </pre>
     *
     * @return An {@link String} with the default language
     * @throws RuntimeException If no variable was defined in the application.properties
     */
    public String getDefaultLanguage() {
        if (StringUtils.isNullOrEmpty(defaultLanguage)) {
            throw new RuntimeException("Please specify the default language in 'application.properties'.");
        }

        return defaultLanguage;
    }

    /**
     * Method to set the default language of the HawAPI project.
     * <p> This method will override the value defined in the application.properties
     */
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Method to get an {@link List} of the default languages of the HawAPI project.
     * <p> This value is defined in the application.properties using:
     * <pre>
     * com.lucasjosino.hawapi.application.languages
     * </pre>
     *
     * @return An {@link String} with the default language
     * @throws RuntimeException If no variable was defined in the application.properties
     */
    public List<String> getLanguages() {
        if (languages == null || languages.isEmpty()) {
            throw new RuntimeException("Please specify the supported languages in 'application.properties'.");
        }

        return languages;
    }

    /**
     * Method to set an {@link List} fo the default languages of the HawAPI project.
     * <p> This method will override the value defined in the application.properties
     */
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}

