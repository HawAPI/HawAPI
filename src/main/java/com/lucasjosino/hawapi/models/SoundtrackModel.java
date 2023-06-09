package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "soundtracks")
public class SoundtrackModel extends BaseModel {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private String artist;

    @Column
    private String album;

    @JsonProperty("release_date")
    @Column(nullable = false, name = "release_date")
    private LocalDate releaseDate;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<String> urls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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
        return "SoundtrackModel{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", releaseDate=" + releaseDate +
                ", urls=" + urls +
                '}';
    }
}
