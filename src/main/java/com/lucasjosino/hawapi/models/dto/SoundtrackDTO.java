package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class SoundtrackDTO extends BaseDTO {

    @Size(max = 5)
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

    @Size(max = 10, message = "Field 'urls' cannot exceed 10 urls")
    private List<@BasicURL(message = "Field 'urls' doesn't have valid URLs") String> urls;

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

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
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
                ", urls=" + urls +
                '}';
    }
}
