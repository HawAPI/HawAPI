package com.lucasjosino.hawapi.models.translations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.models.base.BaseTranslation;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "locations_translations")
@JsonIgnoreProperties("location_uuid")
public class LocationTranslation extends BaseTranslation {

    @JsonProperty("location_uuid")
    @Column(name = "location_uuid")
    private UUID locationUuid;

    @Column
    private String name;

    @Column
    private String description;

    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_uuid", insertable = false, updatable = false)
    private LocationModel location;

    public UUID getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(UUID locationUuid) {
        this.locationUuid = locationUuid;
    }

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

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "LocationTranslation{" +
                "locationUuid=" + locationUuid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                '}';
    }
}
