package com.lucasjosino.hawapi.models.user;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NotNull
@NotEmpty
public class UserAuthenticationModel {

    private String nickname;

    private String email;

    private String password;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
}
