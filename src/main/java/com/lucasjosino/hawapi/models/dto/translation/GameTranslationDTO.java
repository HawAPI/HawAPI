package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;
import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class GameTranslationDTO extends BaseTranslationDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'name' is required")
    private String name;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @BasicURL
    @NotBlank(message = "Field 'trailer' is required")
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
