package com.lucasjosino.hawapi.models.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserAuthDTO {

    @Size(max = 30)
    @NotBlank(message = "Field 'username' is required")
    private String username;

    @Email
    @Size(max = 50)
    @NotBlank(message = "Field 'email' is required")
    private String email;

    @Size(min = 8, max = 24)
    @NotBlank(message = "Field 'password' is required")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserAuthDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
