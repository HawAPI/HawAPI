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

    @Column
    private Integer playtime;

    @Column(name = "age_rating")
    private String ageRating;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> stores;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> modes;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> platforms;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> publishers;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> developers;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> tags;

    @Column(nullable = false, name = "release_date")
    private LocalDate releaseDate;

    @Column(nullable = false)
    private String website;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> languages;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<String> images;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL)
    private GameTranslation translation;

    public Integer getPlaytime() {
        return playtime;
    }

    public void setPlaytime(Integer playtime) {
        this.playtime = playtime;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public List<String> getStores() {
        return stores;
    }

    public void setStores(List<String> stores) {
        this.stores = stores;
    }

    public List<String> getModes() {
        return modes;
    }

    public void setModes(List<String> modes) {
        this.modes = modes;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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
                "playtime=" + playtime +
                ", ageRating='" + ageRating + '\'' +
                ", stores=" + stores +
                ", modes=" + modes +
                ", platforms=" + platforms +
                ", publishers=" + publishers +
                ", developers=" + developers +
                ", tags=" + tags +
                ", releaseDate=" + releaseDate +
                ", website='" + website + '\'' +
                ", languages=" + languages +
                ", images=" + images +
                ", translation=" + translation +
                '}';
    }
}
