package com.lucasjosino.hawapi.filters;

import com.lucasjosino.hawapi.filters.base.BaseTranslationFilter;

import java.time.LocalDate;

public class SeasonFilter extends BaseTranslationFilter {

    private String title;

    private String description;

    private Integer durationTotal;

    private Byte seasonNum;

    private LocalDate releaseDate;

    private Integer budget;

    private String[] genres;

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

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }
}