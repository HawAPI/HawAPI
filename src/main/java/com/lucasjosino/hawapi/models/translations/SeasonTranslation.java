package com.lucasjosino.hawapi.models.translations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.base.BaseTranslation;
import com.lucasjosino.hawapi.validators.annotations.BasicURL;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "seasons_translations")
@JsonIgnoreProperties("season_uuid")
public class SeasonTranslation extends BaseTranslation {

    @JsonProperty("season_uuid")
    @Column(name = "season_uuid")
    private UUID seasonUuid;

    @Column
    private String title;

    @Column
    private String description;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> genres;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<@BasicURL String> trailers;

    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "season_uuid", insertable = false, updatable = false)
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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<String> trailers) {
        this.trailers = trailers;
    }

    public SeasonModel getSeason() {
        return season;
    }

    public void setSeason(SeasonModel season) {
        this.season = season;
    }

    @Override
    public String toString() {
        return "SeasonTranslation{" +
                "seasonUuid=" + seasonUuid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", genres=" + genres +
                ", trailers=" + trailers +
                ", season=" + season +
                '}';
    }
}
