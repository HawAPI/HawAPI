package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Arrays;

public class GameDTO extends BaseDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'name' is required")
    private String name;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @Size(max = 5)
    @NotBlank(message = "Field 'language' is required")
    private String language;

    private String[] platforms;

    @Size(max = 10, message = "Field 'genres' cannot exceed 10 names")
    private String[] genres;

    @Size(max = 5, message = "Field 'publishers' cannot exceed 10 names")
    private String[] publishers;

    @Size(max = 10, message = "Field 'developers' cannot exceed 10 names")
    private String[] developers;

    @JsonProperty("release_date")
    @NotNull(message = "Field 'release_date' is required")
    private LocalDate releaseDate;

    @NotBlank(message = "Field 'url' is required")
    @BasicURL(message = "Field 'url' doesn't have a valid URL")
    private String url;

    private String[] languages;

    @NotNull(message = "Field 'trailer' is required")
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getPublishers() {
        return publishers;
    }

    public void setPublishers(String[] publishers) {
        this.publishers = publishers;
    }

    public String[] getDevelopers() {
        return developers;
    }

    public void setDevelopers(String[] developers) {
        this.developers = developers;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    @Override
    public String toString() {
        return "GameDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", platforms=" + Arrays.toString(platforms) +
                ", genres=" + Arrays.toString(genres) +
                ", publishers=" + Arrays.toString(publishers) +
                ", developers=" + Arrays.toString(developers) +
                ", releaseDate=" + releaseDate +
                ", url='" + url + '\'' +
                ", languages=" + Arrays.toString(languages) +
                ", trailer='" + trailer + '\'' +
                '}';
    }
}
