package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.models.ActorSocialModel;
import com.lucasjosino.hawapi.models.base.BaseDTO;

import java.time.LocalDate;
import java.util.List;

public class ActorDTO extends BaseDTO {

    private String firstName;

    private String lastName;

    private String[] nicknames;

    private LocalDate birthDate;

    private LocalDate deathDate;

    private Byte gender;

    private String nationality;

    private String[] seasons;

    private String[] awards;

    private String character;

    private List<ActorSocialModel> socials;

    private String[] images;

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

    public String[] getSeasons() {
        return seasons;
    }

    public void setSeasons(String[] seasons) {
        this.seasons = seasons;
    }

    public String[] getAwards() {
        return awards;
    }

    public void setAwards(String[] awards) {
        this.awards = awards;
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
