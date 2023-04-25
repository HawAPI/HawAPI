package com.lucasjosino.hawapi.filters;

import com.lucasjosino.hawapi.filters.base.BaseFilter;

public class LocationFilter extends BaseFilter {

    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}