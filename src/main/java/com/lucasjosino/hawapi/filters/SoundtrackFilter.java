package com.lucasjosino.hawapi.filters;

import com.lucasjosino.hawapi.filters.base.BaseFilter;

public class SoundtrackFilter extends BaseFilter {

    private String name;

    private String artist;

    private String album;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
