package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "actors_socials")
public class ActorSocialModel implements Serializable {

    @Id
    @JsonIgnore
    @Column(nullable = false, insertable = false, updatable = false)
    private int id;

    @Column(nullable = false)
    private String social;

    @Column(nullable = false)
    private String handle;

    @Column(nullable = false)
    private String url;

    @JsonProperty("actor_uuid")
    @Column(name = "actor_uuid", nullable = false)
    private UUID actorUuid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getActorUuid() {
        return actorUuid;
    }

    public void setActorUuid(UUID actorUuid) {
        this.actorUuid = actorUuid;
    }
}
