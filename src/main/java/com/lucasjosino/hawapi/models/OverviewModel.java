package com.lucasjosino.hawapi.models;

import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.models.translations.OverviewTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "overviews")
public class OverviewModel extends BaseModel {

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> languages;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> creators;

    @OneToOne(mappedBy = "overview", cascade = CascadeType.ALL)
    private OverviewTranslation translation;

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
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
                "languages=" + languages +
                ", creators=" + creators +
                ", translation=" + translation +
                '}';
    }
}
