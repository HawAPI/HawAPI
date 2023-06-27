package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class CharacterDTO extends BaseDTO {

    @Size(max = 50)
    @JsonProperty("first_name")
    @NotBlank(message = "Field 'first_name' is required")
    private String firstName;

    @Size(max = 50)
    @JsonProperty("last_name")
    @NotBlank(message = "Field 'last_name' is required")
    private String lastName;

    @Size(max = 10, message = "Field 'nicknames' cannot exceed 10 items")
    private List<String> nicknames;

    @PastOrPresent
    @JsonProperty("birth_date")
    @NotNull(message = "Field 'birth_date' is required")
    private LocalDate birthDate;

    @PastOrPresent
    @JsonProperty("death_date")
    @NotNull(message = "Field 'death_date' is required")
    private LocalDate deathDate;

    @NotNull(message = "Field 'gender' is required")
    @PositiveOrZero(message = "Field 'gender' cannot be negative")
    @Max(value = 9, message = "Field 'gender' can only be defined as: 0, 1, 2 or 9")
    private Byte gender;

    private String actor;

    private List<@BasicURL(image = true) String> images;

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

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "CharacterDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nicknames=" + nicknames +
                ", birthDate=" + birthDate +
                ", deathDate=" + deathDate +
                ", gender=" + gender +
                ", actor='" + actor + '\'' +
                ", images=" + images +
                '}';
    }
}
