package com.lucasjosino.hawapi.models.translations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.base.BaseTranslation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "seasons_translations")
@JsonIgnoreProperties("season_uuid")
public class SeasonTranslation extends BaseTranslation {

    @JsonProperty("season_uuid")
    @Column(name = "season_uuid", insertable = false, updatable = false)
    private UUID seasonUuid;

    @Column
    private String title;

    @Column
    private String description;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] genres;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] trailers;

    @JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "season_uuid", nullable = false)
    private SeasonModel season;

    public UUID getSeasonUuid() {
        return seasonUuid;
    }

    public void setSeasonUuid(UUID seasonUuid) {
        this.seasonUuid = seasonUuid;
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

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getTrailers() {
        return trailers;
    }

    public void setTrailers(String[] trailers) {
        this.trailers = trailers;
    }

    public SeasonModel getSeason() {
        return season;
    }

    public void setSeason(SeasonModel season) {
        this.season = season;
    }
}
