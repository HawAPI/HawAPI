package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;

public class GameTranslationDTO extends BaseTranslationDTO {

    private String name;

    private String description;

    private String trailer;

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

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    @Override
    public String toString() {
        return "GameTranslationDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", trailer='" + trailer + '\'' +
                '}';
    }
}
