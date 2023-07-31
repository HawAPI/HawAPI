package com.lucasjosino.hawapi.models.dto.translation;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;
import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class GameTranslationDTO extends BaseTranslationDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'name' is required")
    private String name;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @Size(max = 10, message = "Field 'genres' cannot exceed 10 items")
    private List<String> genres;

    @NotBlank(message = "Field 'trailer' is required")
    @BasicURL(message = "Field 'trailer' doesn't have a valid URL")
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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
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
                ", genres=" + genres +
                ", trailer='" + trailer + '\'' +
                '}';
    }
}
