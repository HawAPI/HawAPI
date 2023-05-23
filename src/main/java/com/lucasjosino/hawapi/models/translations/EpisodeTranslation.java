package com.lucasjosino.hawapi.models.translations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.base.BaseTranslation;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "episodes_translations")
@JsonIgnoreProperties("episode_uuid")
public class EpisodeTranslation extends BaseTranslation {

    @JsonProperty("episode_uuid")
    @Column(name = "episode_uuid")
    private UUID episodeUuid;

    @Column
    private String title;

    @Column
    private String description;

    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_uuid", insertable = false, updatable = false)
    private EpisodeModel episode;

    public EpisodeModel getEpisode() {
        return episode;
    }

    public void setEpisode(EpisodeModel episode) {
        this.episode = episode;
    }

    public UUID getEpisodeUuid() {
        return episodeUuid;
    }

    public void setEpisodeUuid(UUID episodeUuid) {
        this.episodeUuid = episodeUuid;
    }

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

    @Override
    public String toString() {
        return "EpisodeTranslation{" +
                "episodeUuid=" + episodeUuid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", episode=" + episode +
                '}';
    }
}