package com.lucasjosino.hawapi.models;

public class APIModel {

    private final String name;
    private final String description;
    private final String version;
    private final String github;
    private final String url;
    private final String apiUrl;
    private final String apiRouter;

    public APIModel(String name, String description, String version, String github, String url, String apiUrl, String apiRouter) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.github = github;
        this.url = url;
        this.apiUrl = apiUrl;
        this.apiRouter = apiRouter;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getGithub() {
        return github;
    }

    public String getUrl() {
        return url;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiRouter() {
        return apiRouter;
    }
}
