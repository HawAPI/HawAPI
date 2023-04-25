package com.lucasjosino.hawapi.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.filters.base.BaseFilter;

public class EpisodeFilter extends BaseFilter {

    private String title;

    private String description;

    private Integer duration;

    @JsonProperty("episode_num")
    private Byte episodeNum;

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
}