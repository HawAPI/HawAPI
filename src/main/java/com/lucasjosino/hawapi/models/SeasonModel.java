package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "seasons")
public class SeasonModel extends BaseModel {

    @JsonProperty("duration_total")
    @Column(nullable = false, name = "duration_total")
    private Integer durationTotal;

    @JsonProperty("season_num")
    @Column(nullable = false, name = "season_num")
    private Byte seasonNum;

    @JsonProperty("release_date")
    @Column(nullable = false, name = "release_date")
    private LocalDate releaseDate;

    @JsonProperty("next_season")
    @Column(name = "next_season")
    private String nextSeason;

    @JsonProperty("prev_season")
    @Column(name = "prev_season")
    private String prevSeason;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] episodes;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] soundtracks;

    @Column
    private Integer budget;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] images;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] languages;

    @Column
    private String thumbnail;

    @OneToOne(mappedBy = "season", cascade = CascadeType.ALL)
    private SeasonTranslation translation;

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

    public SeasonTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(SeasonTranslation translation) {
        this.translation = translation;
    }
}
