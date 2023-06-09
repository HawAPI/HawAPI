package com.lucasjosino.hawapi.models.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract public class BaseTranslationDTO implements Serializable {

    @Size(max = 5)
    @NotBlank(message = "Field 'language' is required")
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "BaseTranslationDTO{" +
                "language='" + language + '\'' +
                '}';
    }
}