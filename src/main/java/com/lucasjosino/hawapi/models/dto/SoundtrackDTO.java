package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Arrays;

public class SoundtrackDTO extends BaseDTO {

    @NotBlank(message = "Field 'name' is required")
    private String name;

    @NotNull(message = "Field 'duration' is required")
    private Integer duration;

    @NotBlank(message = "Field 'artist' is required")
    private String artist;

    private String album;

    @JsonProperty("release_date")
    @NotNull(message = "Field 'release_date' is required")
    private LocalDate releaseDate;

    private String[] urls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "SoundtrackDTO{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", releaseDate=" + releaseDate +
                ", urls=" + Arrays.toString(urls) +
                '}';
    }
}
