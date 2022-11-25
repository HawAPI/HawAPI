package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "actors")
public class ActorModel extends BaseModel {
    @JsonProperty("first_name")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] nicknames;

    @JsonProperty("birth_date")
    @Column(name = "birth_date")
    private String birthDate;

    @JsonProperty("death_date")
    @Column(name = "death_date")
    private String deathDate;

    @Column
    private String gender;

    @Column
    private String nationality;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] episodes;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] seasons;

    @Type(type = "string-array")
    @Column(columnDefinition = "varchar[]")
    private String[] awards;

    @Column(nullable = false)
    private String character;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ActorSocialModel.class)
    @JoinColumn(name = "socials")
    @JoinTable(name = "actors_socials",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "actor_id")
    )
    private List<ActorSocialModel> socials;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] images;

    @Column
    private String thumbnail;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String[] getNicknames() {
        return nicknames;
    }

    public void setNicknames(String[] nicknames) {
        this.nicknames = nicknames;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String[] getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String[] episodes) {
        this.episodes = episodes;
    }

    public String[] getSeasons() {
        return seasons;
    }

    public void setSeasons(String[] seasons) {
        this.seasons = seasons;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public List<ActorSocialModel> getSocials() {
        return socials;
    }

    public void setSocials(List<ActorSocialModel> socials) {
        this.socials = socials;
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
