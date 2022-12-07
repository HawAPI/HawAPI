package com.lucasjosino.hawapi.filters;

import com.lucasjosino.hawapi.filters.base.BaseFilter;

import java.util.List;

public class GameFilter extends BaseFilter {

    private String name;

    private List<String> platforms;

    private List<String> genres;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
