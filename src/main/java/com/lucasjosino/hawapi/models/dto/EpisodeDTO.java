package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;

public class EpisodeDTO extends BaseDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'title' is required")
    private String title;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @Size(max = 5)
    @NotBlank(message = "Field 'language' is required")
    private String language;

    @NotNull(message = "Field 'duration' is required")
    private Integer duration;

    @JsonProperty("episode_num")
    @NotNull(message = "Field 'episode_num' is required")
    private Byte episodeNum;

    @Size(max = 255)
    @JsonProperty("next_episode")
    @NotBlank(message = "Field 'next_episode' is required")
    private String nextEpisode;

    @Size(max = 255)
    @JsonProperty("prev_episode")
    @NotBlank(message = "Field 'prev_episode' is required")
    private String prevEpisode;

    @Size(max = 255)
    @NotBlank(message = "Field 'season' is required")
    private String season;

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
                '}';
    }
}
