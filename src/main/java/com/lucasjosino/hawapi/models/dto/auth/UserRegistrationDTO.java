package com.lucasjosino.hawapi.models.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegistrationDTO {

    @Size(max = 30)
    @JsonProperty("first_name")
    @NotBlank(message = "Field 'first_name' is required")
    private String firstName;

    @Size(max = 30)
    @JsonProperty("last_name")
    @NotBlank(message = "Field 'last_name' is required")
    private String lastName;

    @Size(max = 30)
    @NotBlank(message = "Field 'username' is required")
    private String username;

    @Email
    @Size(max = 50)
    @NotBlank(message = "Field 'email' is required")
    private String email;

    @Size(max = 15)
    @Pattern(
            regexp = "^(BASIC|DEV|ADMIN|MAINTAINER)$",
            message = "For the account type only the values BASIC, DEV, MAINTAINER or ADMIN are accepted."
    )
    private String role;

    @Size(min = 8, max = 24)
    @NotBlank(message = "Field 'password' is required")
    private String password;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserRegistrationDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
