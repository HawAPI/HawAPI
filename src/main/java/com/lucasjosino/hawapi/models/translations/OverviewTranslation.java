package com.lucasjosino.hawapi.models.translations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.OverviewModel;
import com.lucasjosino.hawapi.models.base.BaseTranslation;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "overviews_translations")
@JsonIgnoreProperties("overview_uuid")
public class OverviewTranslation extends BaseTranslation {

    @JsonProperty("overview_uuid")
    @Column(name = "overview_uuid")
    private UUID overviewUuid;

    @Column
    private String title;

    @Column
    private String description;

    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "overview_uuid", insertable = false, updatable = false)
    private OverviewModel overview;

    public UUID getOverviewUuid() {
        return overviewUuid;
    }

    public void setOverviewUuid(UUID overviewUuid) {
        this.overviewUuid = overviewUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OverviewModel getOverview() {
        return overview;
    }

    public void setOverview(OverviewModel overview) {
        this.overview = overview;
    }

    @Override
    public String toString() {
        return "OverviewTranslation{" +
                "overviewUuid=" + overviewUuid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", overview=" + overview +
                '}';
    }
}
