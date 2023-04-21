package com.lucasjosino.hawapi.models.translations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.base.BaseTranslation;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "games_translations")
@JsonIgnoreProperties("game_uuid")
public class GameTranslation extends BaseTranslation {

    @JsonProperty("game_uuid")
    @Column(name = "game_uuid", insertable = false, updatable = false)
    private UUID gameUuid;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String trailer;

    @JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "game_uuid", nullable = false)
    private GameModel game;

    public UUID getGameUuid() {
        return gameUuid;
    }

    public void setGameUuid(UUID gameUuid) {
        this.gameUuid = gameUuid;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public GameModel getGame() {
        return game;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }
}
