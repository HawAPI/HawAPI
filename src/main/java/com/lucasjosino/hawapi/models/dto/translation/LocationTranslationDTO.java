package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;

public class LocationTranslationDTO extends BaseTranslationDTO {

    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
