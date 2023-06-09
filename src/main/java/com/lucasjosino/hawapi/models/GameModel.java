package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.GameTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "games")
public class GameModel extends BaseModel {

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> platforms;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> genres;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> publishers;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> developers;

    @Column(nullable = false, name = "release_date")
    private LocalDate releaseDate;

    @Column(nullable = false)
    private String url;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<String> languages;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL)
    private GameTranslation translation;

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
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

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public GameTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(GameTranslation translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "GameModel{" +
                "platforms=" + platforms +
                ", genres=" + genres +
                ", publishers=" + publishers +
                ", developers=" + developers +
                ", releaseDate=" + releaseDate +
                ", url='" + url + '\'' +
                ", languages=" + languages +
                ", translation=" + translation +
                '}';
    }
}
