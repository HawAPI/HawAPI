package com.lucasjosino.hawapi.models.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@MappedSuperclass
abstract public class BaseModel {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String href;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] sources;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String[] getSources() {
        return sources;
    }

    public void setSources(String[] sources) {
        this.sources = sources;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String created_at) {
        this.createdAt = created_at;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updated_at) {
        this.updatedAt = updated_at;
    }
}
