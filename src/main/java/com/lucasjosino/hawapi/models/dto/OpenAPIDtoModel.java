package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.configs.APIConfig;

public class OpenAPIDtoModel {
    private String title;

    private String description;

    private String version;

    private String url;

    private String docs;

    private String github;

    @JsonProperty("github_url")
    private String githubUrl;

    @JsonProperty("api_url")
    private String apiUrl;

    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("api_base_url")
    private String apiBaseUrl;

    private String license;

    @JsonProperty("license_url")
    private String licenseUrl;

    public OpenAPIDtoModel() {
        this.title = APIConfig.title;
        this.description = APIConfig.description;
        this.version = APIConfig.version;
        this.url = APIConfig.url;
        this.docs = APIConfig.docs;
        this.github = APIConfig.github;
        this.githubUrl = APIConfig.githubUrl;
        this.apiUrl = APIConfig.apiUrl;
        this.apiVersion = APIConfig.apiVersion;
        this.apiBaseUrl = APIConfig.apiBaseUrl;
        this.license = APIConfig.license;
        this.licenseUrl = APIConfig.licenseUrl;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDocs() {
        return docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }
}
