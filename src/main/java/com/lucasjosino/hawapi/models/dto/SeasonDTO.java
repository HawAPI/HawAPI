package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.models.base.BaseDTO;

import java.time.LocalDate;

public class SeasonDTO extends BaseDTO {

    private String title;

    private String description;

    private Integer durationTotal;

    private String[] genres;

    private Byte seasonNum;

    private LocalDate releaseDate;

    private String nextSeason;

    private String prevSeason;

    private String[] episodes;

    private String[] soundtracks;

    private String[] trailers;

    private Integer budget;

    private String[] images;

    private String[] languages;

    private String thumbnail;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
