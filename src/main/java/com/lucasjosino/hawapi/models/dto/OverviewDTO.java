package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.models.base.BaseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class OverviewDTO extends BaseDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'title' is required")
    private String title;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @Size(max = 5)
    @NotBlank(message = "Field 'language' is required")
    private String language;

    @NotEmpty(message = "Field 'languages' is required")
    private List<String> languages;

    @Size(max = 10, message = "Field 'creators' cannot exceed 10 items")
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

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
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
                ", languages=" + languages +
                ", creators=" + creators +
                '}';
    }
}
