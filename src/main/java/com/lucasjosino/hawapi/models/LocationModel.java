package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locations")
public class LocationModel extends BaseModel {

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<String> images;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> languages;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    private LocationTranslation translation;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public LocationTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(LocationTranslation translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "images=" + images +
                ", languages=" + languages +
                ", translation=" + translation +
                '}';
    }
}
