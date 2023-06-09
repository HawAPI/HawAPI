package com.lucasjosino.hawapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "actors")
public class ActorModel extends BaseModel {

    @JsonProperty("first_name")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> nicknames;

    @JsonProperty("birth_date")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @JsonProperty("death_date")
    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column
    private Byte gender;

    @Column
    private String nationality;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> seasons;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> awards;

    @Column(nullable = false)
    private String character;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "actor_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private Set<ActorSocialModel> socials;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    private List<String> images;

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

    public List<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<String> seasons) {
        this.seasons = seasons;
    }

    public List<String> getAwards() {
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = awards;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public Set<ActorSocialModel> getSocials() {
        return socials;
    }

    public void setSocials(Set<ActorSocialModel> socials) {
        this.socials = socials;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "ActorModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nicknames=" + nicknames +
                ", birthDate=" + birthDate +
                ", deathDate=" + deathDate +
                ", gender=" + gender +
                ", nationality='" + nationality + '\'' +
                ", seasons=" + seasons +
                ", awards=" + awards +
                ", character='" + character + '\'' +
                ", socials=" + socials +
                ", images=" + images +
                '}';
    }
}
