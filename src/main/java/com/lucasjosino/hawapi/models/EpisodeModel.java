package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "episodes")
public class EpisodeModel extends BaseModel {

    @Column(nullable = false)
    private Integer duration;

    @JsonProperty("episode_num")
    @Column(nullable = false, name = "episode_num")
    private Byte episodeNum;

    @JsonProperty("next_episode")
    @Column(name = "next_episode")
    private String nextEpisode;

    @JsonProperty("prev_episode")
    @Column(name = "prev_episode")
    private String prevEpisode;

    @Column(nullable = false)
    private String season;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<String> images;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> languages;

    @OneToOne(mappedBy = "episode", cascade = CascadeType.ALL)
    private EpisodeTranslation translation;

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

    public EpisodeTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(EpisodeTranslation translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "EpisodeModel{" +
                "duration=" + duration +
                ", episodeNum=" + episodeNum +
                ", nextEpisode='" + nextEpisode + '\'' +
                ", prevEpisode='" + prevEpisode + '\'' +
                ", season='" + season + '\'' +
                ", images=" + images +
                ", languages=" + languages +
                ", translation=" + translation +
                '}';
    }
}
