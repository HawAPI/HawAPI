package com.lucasjosino.hawapi.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.filters.base.BaseFilter;

import java.time.LocalDate;

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