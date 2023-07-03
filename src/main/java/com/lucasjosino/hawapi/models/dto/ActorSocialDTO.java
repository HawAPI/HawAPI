package com.lucasjosino.hawapi.models.dto;

import com.lucasjosino.hawapi.validators.annotations.BasicURL;

import javax.validation.constraints.NotBlank;

public class ActorSocialDTO {

    @NotBlank(message = "Field 'social' is required")
    private String social;

    @NotBlank(message = "Field 'handle' is required")
    private String handle;

    @BasicURL
    @NotBlank(message = "Field 'url' is required")
    private String url;

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ActorSocialDTO{" +
                "social='" + social + '\'' +
                ", handle='" + handle + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
