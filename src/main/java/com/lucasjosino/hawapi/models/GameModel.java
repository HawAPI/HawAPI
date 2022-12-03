package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "games")
public class GameModel extends BaseModel {

    @Column(nullable = false)
    private String name;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] platforms;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] genres;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] publishers;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] developers;

    @Column(nullable = false, name = "release_date")
    private LocalDate releaseDate;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String trailer;

    @Column
    private String thumbnail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
