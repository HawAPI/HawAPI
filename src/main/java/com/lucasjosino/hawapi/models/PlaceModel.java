package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PlaceModel extends BaseModel {
    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String[] images;

    @Column
    private String thumbnail;

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
}
