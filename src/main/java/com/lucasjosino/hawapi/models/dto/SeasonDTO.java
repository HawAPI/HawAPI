package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Arrays;

public class SeasonDTO extends BaseDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'title' is required")
    private String title;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @Size(max = 5)
    @NotBlank(message = "Field 'language' is required")
    private String language;

    @JsonProperty("duration_total")
    @Positive(message = "The value must be positive")
    @NotNull(message = "Field 'duration_total' is required")
    private Integer durationTotal;

    @Size(max = 10, message = "Field 'genres' cannot exceed 10 names")
    private String[] genres;

    @JsonProperty("season_num")
    @Positive(message = "The value must be positive")
    @NotNull(message = "Field 'season_num' is required")
    private Byte seasonNum;

    @JsonProperty("release_date")
    @NotNull(message = "Field 'release_date' is required")
    private LocalDate releaseDate;

    @JsonProperty("next_season")
    private String nextSeason;

    @JsonProperty("prev_season")
    private String prevSeason;

    private String[] episodes;

    @Size(max = 10, message = "Field 'soundtracks' cannot exceed 10 soundtracks")
    private String[] soundtracks;

    @Size(max = 10, message = "Field 'trailers' cannot exceed 10 trailers")
    private String[] trailers;

    @Positive(message = "Field 'budget' value must be positive")
    private Integer budget;

    private String[] images;

    private String[] languages;

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

    public Integer getDurationTotal() {
        return durationTotal;
    }

    public void setDurationTotal(Integer durationTotal) {
        this.durationTotal = durationTotal;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public Byte getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(Byte seasonNum) {
        this.seasonNum = seasonNum;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getNextSeason() {
        return nextSeason;
    }

    public void setNextSeason(String nextSeason) {
        this.nextSeason = nextSeason;
    }

    public String getPrevSeason() {
        return prevSeason;
    }

    public void setPrevSeason(String prevSeason) {
        this.prevSeason = prevSeason;
    }

    public String[] getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String[] episodes) {
        this.episodes = episodes;
    }

    public String[] getSoundtracks() {
        return soundtracks;
    }

    public void setSoundtracks(String[] soundtracks) {
        this.soundtracks = soundtracks;
    }

    public String[] getTrailers() {
        return trailers;
    }

    public void setTrailers(String[] trailers) {
        this.trailers = trailers;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return "SeasonDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", durationTotal=" + durationTotal +
                ", genres=" + Arrays.toString(genres) +
                ", seasonNum=" + seasonNum +
                ", releaseDate=" + releaseDate +
                ", nextSeason='" + nextSeason + '\'' +
                ", prevSeason='" + prevSeason + '\'' +
                ", episodes=" + Arrays.toString(episodes) +
                ", soundtracks=" + Arrays.toString(soundtracks) +
                ", trailers=" + Arrays.toString(trailers) +
                ", budget=" + budget +
                ", images=" + Arrays.toString(images) +
                ", languages=" + Arrays.toString(languages) +
                '}';
    }
}
