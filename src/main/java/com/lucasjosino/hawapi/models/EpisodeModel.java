package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;

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

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] images;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] languages;

    @Column
    private String thumbnail;

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

    public EpisodeTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(EpisodeTranslation translation) {
        this.translation = translation;
    }
}
