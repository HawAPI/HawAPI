package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.models.base.BaseDTO;

import java.util.List;

public class OverviewDTO extends BaseDTO {

    private String title;

    private String description;

    private String language;

    private List<String> creators;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = creators;
    }

    @Override
    public String toString() {
        return "OverviewDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", creators=" + creators +
                '}';
    }
}
