package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "locations")
public class LocationModel extends BaseModel {

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] images;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] languages;

    @Column
    private String thumbnail;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    private LocationTranslation translation;

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public LocationTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(LocationTranslation translation) {
        this.translation = translation;
    }
}
