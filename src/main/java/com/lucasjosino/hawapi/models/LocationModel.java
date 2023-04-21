package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "locations")
public class LocationModel extends BaseModel {

    @Column(nullable = false)
    private transient String name;

    @Column(nullable = false)
    private transient String description;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] images;

    @Column
    private String thumbnail;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    private LocationTranslation translation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
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
