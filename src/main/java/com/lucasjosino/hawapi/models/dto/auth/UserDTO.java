package com.lucasjosino.hawapi.models.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class UserDTO {

    @Max(value = 30)
    @JsonProperty("first_name")
    @NotBlank(message = "Field 'first_name' is required")
    private String firstName;

    @Max(value = 30)
    @JsonProperty("last_name")
    @NotBlank(message = "Field 'last_name' is required")
    private String lastName;

    @Max(value = 30)
    @NotBlank(message = "Field 'username' is required")
    private String username;

    @Email
    @Max(value = 50)
    @NotBlank(message = "Field 'email' is required")
    private String email;

    @Max(value = 15)
    @Pattern(
            regexp = "^(BASIC|DEV|ADMIN)$",
            message = "For the account type only the values BASIC, DEV, or ADMIN are accepted."
    )
    private String role;

    private String token;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
