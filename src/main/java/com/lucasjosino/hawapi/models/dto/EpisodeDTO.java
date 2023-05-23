package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.models.base.BaseDTO;

import java.util.Arrays;

public class EpisodeDTO extends BaseDTO {

    private String title;

    private String description;

    private String language;

    private Integer duration;

    private Byte episodeNum;

    private String nextEpisode;

    private String prevEpisode;

    private String season;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Byte getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(Byte episodeNum) {
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

    @Override
    public String toString() {
        return "EpisodeDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", duration=" + duration +
                ", episodeNum=" + episodeNum +
                ", nextEpisode='" + nextEpisode + '\'' +
                ", prevEpisode='" + prevEpisode + '\'' +
                ", season='" + season + '\'' +
                ", images=" + Arrays.toString(images) +
                ", languages=" + Arrays.toString(languages) +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
