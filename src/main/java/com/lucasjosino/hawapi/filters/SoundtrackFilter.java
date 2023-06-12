package com.lucasjosino.hawapi.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.controllers.api.v1.SoundtrackController;
import com.lucasjosino.hawapi.filters.base.BaseFilter;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.SoundtrackService;

import java.time.LocalDate;

/**
 * Soundtrack filter model
 *
 * @author Lucas Josino
 * @see SoundtrackModel
 * @see SoundtrackDTO
 * @see SoundtrackController
 * @see SoundtrackService
 * @see SoundtrackRepository
 * @see SpecificationBuilder
 * @since 1.0.0
 */
public class SoundtrackFilter extends BaseFilter {

    private String name;

    private String artist;

    private String album;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}