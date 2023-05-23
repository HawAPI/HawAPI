package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.models.base.BaseDTO;

import java.time.LocalDate;
import java.util.Arrays;

public class GameDTO extends BaseDTO {

    private String name;

    private String description;

    private String[] platforms;

    private String[] genres;

    private String[] publishers;

    private String[] developers;

    private LocalDate releaseDate;

    private String url;

    private String[] languages;

    private String trailer;

    private String thumbnail;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
