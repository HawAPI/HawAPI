package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;

public class SeasonTranslationDTO extends BaseTranslationDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'title' is required")
    private String title;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @NotEmpty(message = "Input movie list cannot be empty.")
    private String[] genres;

    private String[] trailers;

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

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getTrailers() {
        return trailers;
    }

    public void setTrailers(String[] trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "SeasonTranslationDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", genres=" + Arrays.toString(genres) +
                ", trailers=" + Arrays.toString(trailers) +
                '}';
    }
}
