package com.lucasjosino.hawapi.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.controllers.api.v1.GameController;
import com.lucasjosino.hawapi.filters.base.BaseTranslationFilter;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.dto.GameDTO;
import com.lucasjosino.hawapi.repositories.GameRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.GameService;

import java.time.LocalDate;

/**
 * Game filter model
 *
 * @author Lucas Josino
 * @see GameModel
 * @see GameDTO
 * @see GameController
 * @see GameService
 * @see GameRepository
 * @see SpecificationBuilder
 * @since 1.0.0
 */
public class GameFilter extends BaseTranslationFilter {

    private String name;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    private transient String[] platforms;

    private transient String[] genres;

    private transient String[] publishers;

    private transient String[] developers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getPublishers() {
        return publishers;
    }

    public void setPublishers(String[] publishers) {
        this.publishers = publishers;
    }

    public String[] getDevelopers() {
        return developers;
    }

    public void setDevelopers(String[] developers) {
        this.developers = developers;
    }
}