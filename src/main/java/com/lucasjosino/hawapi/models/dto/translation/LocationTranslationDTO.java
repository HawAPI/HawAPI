package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LocationTranslationDTO extends BaseTranslationDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'name' is required")
    private String name;

    @NotBlank(message = "Field 'description' is required")
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

    @Override
    public String toString() {
        return "LocationTranslationDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
