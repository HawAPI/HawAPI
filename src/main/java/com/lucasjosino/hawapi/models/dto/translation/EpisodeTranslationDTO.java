package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;

public class EpisodeTranslationDTO extends BaseTranslationDTO {

    private String title;

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
}
