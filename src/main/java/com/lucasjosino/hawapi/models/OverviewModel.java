package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.OverviewTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "overviews")
public class OverviewModel extends BaseModel {

    @Column
    private String thumbnail;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> creators;

    @OneToOne(mappedBy = "overview", cascade = CascadeType.ALL)
    private OverviewTranslation translation;

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = creators;
    }

    public OverviewTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(OverviewTranslation translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "OverviewModel{" +
                "thumbnail='" + thumbnail + '\'' +
                ", creators=" + creators +
                ", translation=" + translation +
                '}';
    }
}
