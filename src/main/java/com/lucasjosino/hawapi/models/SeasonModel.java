package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> episodes;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> soundtracks;

    @Column
    private Integer budget;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<String> images;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> languages;

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

    public List<String> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<String> episodes) {
        this.episodes = episodes;
    }

    public List<String> getSoundtracks() {
        return soundtracks;
    }

    public void setSoundtracks(List<String> soundtracks) {
        this.soundtracks = soundtracks;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public SeasonTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(SeasonTranslation translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "SeasonModel{" +
                "durationTotal=" + durationTotal +
                ", seasonNum=" + seasonNum +
                ", releaseDate=" + releaseDate +
                ", nextSeason='" + nextSeason + '\'' +
                ", prevSeason='" + prevSeason + '\'' +
                ", episodes=" + episodes +
                ", soundtracks=" + soundtracks +
                ", budget=" + budget +
                ", images=" + images +
                ", languages=" + languages +
                ", translation=" + translation +
                '}';
    }
}
