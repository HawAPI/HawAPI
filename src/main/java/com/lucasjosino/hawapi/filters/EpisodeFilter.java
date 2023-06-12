package com.lucasjosino.hawapi.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.controllers.api.v1.EpisodeController;
import com.lucasjosino.hawapi.filters.base.BaseTranslationFilter;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.EpisodeService;

/**
 * Episode filter model
 *
 * @author Lucas Josino
 * @see EpisodeModel
 * @see EpisodeDTO
 * @see EpisodeController
 * @see EpisodeService
 * @see EpisodeRepository
 * @see SpecificationBuilder
 * @since 1.0.0
 */
public class EpisodeFilter extends BaseTranslationFilter {

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