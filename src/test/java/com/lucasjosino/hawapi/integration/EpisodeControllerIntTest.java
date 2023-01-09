package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestConfig
public class EpisodeControllerIntTest extends DatabaseContainerInitializer {

    private static final EpisodeModel episode = getSingleEpisode();

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        episodeRepository.deleteAll();
    }

    @Test
    public void shouldCreateEpisode() throws Exception {
        EpisodeModel episodeToBeSaved = getNewEpisode();

        mockMvc.perform(post("/api/v1/episodes")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(episodeToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(episodeToBeSaved.getTitle()))
                .andExpect(jsonPath("$.description").value(episodeToBeSaved.getDescription()))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episodeToBeSaved.getEpisodeNum())))
                .andExpect(jsonPath("$.season").value(episodeToBeSaved.getSeason()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedCreateEpisode() throws Exception {
        EpisodeModel episodeToBeSaved = getNewEpisode();

        mockMvc.perform(post("/api/v1/episodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(episodeToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenCreateEpisode() throws Exception {
        EpisodeModel episodeToBeSaved = getNewEpisode();

        mockMvc.perform(post("/api/v1/episodes")
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(episodeToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnEpisodeByUUID() throws Exception {
        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(get("/api/v1/episodes/" + episode.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(episode.getUuid().toString()))
                .andExpect(jsonPath("$.href").value(episode.getHref()))
                .andExpect(jsonPath("$.title").value(episode.getTitle()))
                .andExpect(jsonPath("$.description").value(episode.getDescription()))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episode.getEpisodeNum())))
                .andExpect(jsonPath("$.season").value(episode.getSeason()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundEpisode() throws Exception {
        mockMvc.perform(get("/api/v1/episodes/" + episode.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfEpisodes() throws Exception {
        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(get("/api/v1/episodes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyListOfEpisodes() throws Exception {
        mockMvc.perform(get("/api/v1/episodes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnListOfEpisodesWithFilter() throws Exception {
        EpisodeModel episode = getEpisodes().get(1);

        episodeRepository.saveAll(getEpisodes());

        // FIXME: EpisodeFilter will only read if field is camelcase (episodeNum).
        mockMvc.perform(get("/api/v1/episodes")
                        .param("episode_num", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(episode.getUuid().toString()))
                .andExpect(jsonPath("$[0].href").value(episode.getHref()))
                .andExpect(jsonPath("$[0].title").value(episode.getTitle()))
                .andExpect(jsonPath("$[0].description").value(episode.getDescription()))
                .andExpect(jsonPath("$[0].episode_num").value(String.valueOf(episode.getEpisodeNum())))
                .andExpect(jsonPath("$[0].season").value(episode.getSeason()))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnListOfEpisodesWithSortFilter() throws Exception {
        List<EpisodeModel> reversedEpisodes = new ArrayList<>(getEpisodes());
        Collections.reverse(reversedEpisodes);

        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(get("/api/v1/episodes")
                        .param("sort", "DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(reversedEpisodes.get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(reversedEpisodes.get(0).getUuid().toString()));
    }

    @Test
    public void shouldReturnListOfEpisodesWithOrderFilter() throws Exception {
        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(get("/api/v1/episodes")
                        .param("order", "title")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(getEpisodes().get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(getEpisodes().get(0).getUuid().toString()));
    }

    @Test
    public void shouldUpdateEpisode() throws Exception {
        EpisodeModel episodeToBeUpdated = new EpisodeModel();
        episodeToBeUpdated.setTitle("Moa");

        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(patch("/api/v1/episodes/" + episode.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(episodeToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundUpdateEpisode() throws Exception {
        EpisodeModel episodeToBeUpdated = new EpisodeModel();
        episodeToBeUpdated.setTitle("Moa");

        mockMvc.perform(patch("/api/v1/episodes/" + episode.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(episodeToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedUpdateEpisode() throws Exception {
        EpisodeModel episodeToBeUpdated = new EpisodeModel();
        episodeToBeUpdated.setTitle("Moa");

        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(patch("/api/v1/episodes/" + episode.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(episodeToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenUpdateEpisode() throws Exception {
        EpisodeModel episodeToBeUpdated = new EpisodeModel();
        episodeToBeUpdated.setTitle("Moa");

        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(patch("/api/v1/episodes/" + episode.getUuid())
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(episodeToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeleteEpisode() throws Exception {
        episodeRepository.saveAll(getEpisodes());

        mockMvc.perform(delete("/api/v1/episodes/" + episode.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteEpisode() throws Exception {
        mockMvc.perform(delete("/api/v1/episodes/" + episode.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteEpisode() throws Exception {
        mockMvc.perform(delete("/api/v1/episodes/" + episode.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenDeleteEpisode() throws Exception {
        mockMvc.perform(delete("/api/v1/episodes/" + episode.getUuid())
                        .with(getDevAuth())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
