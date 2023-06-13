package com.lucasjosino.hawapi.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.controllers.api.v1.CharacterController;
import com.lucasjosino.hawapi.filters.base.BaseFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.CharacterService;

import java.time.LocalDate;

/**
 * Character filter model
 *
 * @author Lucas Josino
 * @see CharacterModel
 * @see CharacterDTO
 * @see CharacterController
 * @see CharacterService
 * @see CharacterRepository
 * @see SpecificationBuilder
 * @since 1.0.0
 */
public class CharacterFilter extends BaseFilter {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private Byte gender;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @JsonProperty("death_date")
    private LocalDate deathDate;

    private String[] nicknames;

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

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
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

    public String[] getNicknames() {
        return nicknames;
    }

    public void setNicknames(String[] nicknames) {
        this.nicknames = nicknames;
    }
}