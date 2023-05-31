package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class EpisodeTranslationDTO extends BaseTranslationDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'title' is required")
    private String title;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EpisodeTranslationDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
