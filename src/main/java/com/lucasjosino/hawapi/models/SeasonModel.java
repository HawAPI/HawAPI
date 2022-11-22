package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class SeasonModel extends BaseModel {
    @Column
    private String title;

    @Column
    private String description;

    @Column(name = "duration_total")
    private int durationTotal;

    @Column
    private String[] tags;

    @Column
    private String season;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "next_season")
    private String nextSeason;

    @Column(name = "prev_season")
    private String prevSeason;

    @Column
    private String[] episodes;

    @Column
    private String[] trailers;

    @Column
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

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
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
