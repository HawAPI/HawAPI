package com.lucasjosino.hawapi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasjosino.hawapi.models.base.BaseDTO;
import com.lucasjosino.hawapi.repositories.OverviewRepository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

public class OverviewDTO extends BaseDTO {

    @Size(max = 255)
    @NotBlank(message = "Field 'title' is required")
    private String title;

    @NotBlank(message = "Field 'description' is required")
    private String description;

    @Size(max = 5)
    @NotBlank(message = "Field 'language' is required")
    private String language;

    @NotEmpty(message = "Field 'languages' is required")
    private List<String> languages;

    @Size(max = 10, message = "Field 'creators' cannot exceed 10 items")
    private List<String> creators;

    @JsonProperty("data_count")
    private DataCount dataCount;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = creators;
    }

    public DataCount getDataCount() {
        return dataCount;
    }

    public void setDataCount(DataCount dataCount) {
        this.dataCount = dataCount;
    }

    @Override
    public String toString() {
        return "OverviewDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", languages=" + languages +
                ", creators=" + creators +
                ", count=" + dataCount +
                '}';
    }

    /**
     * Data count interface projection. JDBC alias for {@link DataCount}
     *
     * @see DataCount
     * @see OverviewRepository#getAllCounts()
     * @since 1.2.0
     */
    public interface DataCountProjection extends Serializable {

        Long getActors();

        Long getCharacters();

        Long getEpisodes();

        Long getGames();

        Long getLocations();

        Long getSeasons();

        Long getSoundtracks();
    }

    public static class DataCount implements Serializable {

        private Long actors;

        private Long characters;

        private Long episodes;

        private Long games;

        private Long locations;

        private Long seasons;

        private Long soundtracks;

        public Long getActors() {
            return actors;
        }

        public void setActors(Long actors) {
            this.actors = actors;
        }

        public Long getCharacters() {
            return characters;
        }

        public void setCharacters(Long characters) {
            this.characters = characters;
        }

        public Long getEpisodes() {
            return episodes;
        }

        public void setEpisodes(Long episodes) {
            this.episodes = episodes;
        }

        public Long getGames() {
            return games;
        }

        public void setGames(Long games) {
            this.games = games;
        }

        public Long getLocations() {
            return locations;
        }

        public void setLocations(Long locations) {
            this.locations = locations;
        }

        public Long getSeasons() {
            return seasons;
        }

        public void setSeasons(Long seasons) {
            this.seasons = seasons;
        }

        public Long getSoundtracks() {
            return soundtracks;
        }

        public void setSoundtracks(Long soundtracks) {
            this.soundtracks = soundtracks;
        }
    }
}
