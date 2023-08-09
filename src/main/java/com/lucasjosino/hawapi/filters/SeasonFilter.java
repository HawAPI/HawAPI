package com.lucasjosino.hawapi.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.controllers.api.v1.SeasonController;
import com.lucasjosino.hawapi.filters.base.BaseTranslationFilter;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.dto.SeasonDTO;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.SeasonService;

import java.time.LocalDate;

/**
 * Season filter model
 *
 * @author Lucas Josino
 * @see SeasonModel
 * @see SeasonDTO
 * @see SeasonController
 * @see SeasonService
 * @see SeasonRepository
 * @see SpecificationBuilder
 * @since 1.0.0
 */
public class SeasonFilter extends BaseTranslationFilter {

    private String title;

    private String description;

    private Integer durationTotal;

    private Byte seasonNum;

    private LocalDate releaseDate;

    private Integer budget;

    private String[] genres;

    @JsonProperty("next_season")
    private String nextSeason;

    @JsonProperty("prev_season")
    private String prevSeason;

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
}