package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.models.base.BaseDTO;

import java.time.LocalDate;
import java.util.Arrays;

public class CharacterDTO extends BaseDTO {

    private String firstName;

    private String lastName;

    private String[] nicknames;

    private LocalDate birthDate;

    private LocalDate deathDate;

    private Byte gender;

    private String thumbnail;

    private String actor;

    private String[] images;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "CharacterDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nicknames=" + Arrays.toString(nicknames) +
                ", birthDate=" + birthDate +
                ", deathDate=" + deathDate +
                ", gender=" + gender +
                ", thumbnail='" + thumbnail + '\'' +
                ", actor='" + actor + '\'' +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
