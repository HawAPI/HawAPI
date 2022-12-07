package com.lucasjosino.hawapi.filters;

import com.lucasjosino.hawapi.filters.base.BaseFilter;

import java.util.List;

public class SeasonFilter extends BaseFilter {

    private String title;

    private List<String> genres;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
