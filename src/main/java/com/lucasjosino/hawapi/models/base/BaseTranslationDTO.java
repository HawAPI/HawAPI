package com.lucasjosino.hawapi.models.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract public class BaseTranslationDTO implements Serializable {

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}