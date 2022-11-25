package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class SeasonModel extends BaseModel {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @JsonProperty("duration_total")
    @Column(nullable = false, name = "duration_total")
    private int durationTotal;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] tags;

    @JsonProperty("season_num")
    @Column(nullable = false, name = "season_num")
    private String seasonNum;

    @JsonProperty("release_date")
    @Column(nullable = false, name = "release_date")
    private String releaseDate;

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
    @Column(columnDefinition = "text[]")
    private String[] trailers;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] images;

    @Column
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

    public int getDurationTotal() {
        return durationTotal;
    }

    public void setDurationTotal(int durationTotal) {
        this.durationTotal = durationTotal;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(String season) {
        this.seasonNum = season;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getNextSeason() {
        return nextSeason;
    }

    public void setNextEpisode(String nextEpisode) {
        this.nextSeason = nextEpisode;
    }

    public String getPrevSeason() {
        return prevSeason;
    }

    public void setPrevEpisode(String prevEpisode) {
        this.prevSeason = prevEpisode;
    }

    public String[] getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String[] episodes) {
        this.episodes = episodes;
    }

    public String[] getTrailers() {
        return trailers;
    }

    public void setTrailers(String[] trailers) {
        this.trailers = trailers;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
