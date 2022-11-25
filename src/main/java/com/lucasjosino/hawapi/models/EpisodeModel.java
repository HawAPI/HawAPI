package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class EpisodeModel extends BaseModel {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int duration;

    @JsonProperty("episode_num")
    @Column(nullable = false, name = "episode_num")
    private int episodeNum;

    @JsonProperty("next_episode")
    @Column(name = "next_episode")
    private String nextEpisode;

    @JsonProperty("prev_episode")
    @Column(name = "prev_episode")
    private String prevEpisode;

    @Column(nullable = false)
    private String season;

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(int episodeNum) {
        this.episodeNum = episodeNum;
    }

    public String getNextEpisode() {
        return nextEpisode;
    }

    public void setNextEpisode(String nextEpisode) {
        this.nextEpisode = nextEpisode;
    }

    public String getPrevEpisode() {
        return prevEpisode;
    }

    public void setPrevEpisode(String prevEpisode) {
        this.prevEpisode = prevEpisode;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
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
