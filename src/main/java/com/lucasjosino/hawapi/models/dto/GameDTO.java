package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class GameDTO extends BaseDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'name' is required")
    private String name;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @PositiveOrZero
    private Integer playtime;

    @JsonProperty("age_rating")
    private String ageRating;

    @Size(max = 5)
    @NotBlank(message = "Field 'language' is required")
    private String language;

    private List<String> platforms;

    @Size(max = 10, message = "Field 'stores' cannot exceed 10 items")
    private List<@BasicURL String> stores;

    @Size(max = 10, message = "Field 'modes' cannot exceed 10 items")
    private List<String> modes;

    @Size(max = 10, message = "Field 'genres' cannot exceed 10 items")
    private List<String> genres;

    @Size(max = 5, message = "Field 'publishers' cannot exceed 5 items")
    private List<String> publishers;

    @Size(max = 10, message = "Field 'developers' cannot exceed 10 items")
    private List<String> developers;

    @JsonProperty("release_date")
    @NotNull(message = "Field 'release_date' is required")
    private LocalDate releaseDate;

    @NotBlank(message = "Field 'website' is required")
    @BasicURL(message = "Field 'website' doesn't have a valid URL")
    private String website;

    @Size(max = 15, message = "Field 'tags' cannot exceed 15 items")
    private List<String> tags;

    private List<String> languages;

    @NotNull(message = "Field 'trailer' is required")
    @BasicURL(message = "Field 'trailer' doesn't have a valid URL")
    private String trailer;

    private List<@BasicURL(image = true) String> images;

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

    public Integer getPlaytime() {
        return playtime;
    }

    public void setPlaytime(Integer playtime) {
        this.playtime = playtime;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<String> getStores() {
        return stores;
    }

    public void setStores(List<String> stores) {
        this.stores = stores;
    }

    public List<String> getModes() {
        return modes;
    }

    public void setModes(List<String> modes) {
        this.modes = modes;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
        this.developers = developers;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "GameDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", playtime=" + playtime +
                ", ageRating=" + ageRating +
                ", language='" + language + '\'' +
                ", platforms=" + platforms +
                ", stores=" + stores +
                ", modes=" + modes +
                ", genres=" + genres +
                ", publishers=" + publishers +
                ", developers=" + developers +
                ", releaseDate=" + releaseDate +
                ", website='" + website + '\'' +
                ", tags=" + tags +
                ", languages=" + languages +
                ", trailer='" + trailer + '\'' +
                ", images=" + images +
                '}';
    }
}
