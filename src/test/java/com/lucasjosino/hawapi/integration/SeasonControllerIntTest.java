package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import org.junit.jupiter.api.AfterAll;
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
public class SeasonControllerIntTest extends DatabaseContainerInitializer {

    private static final SeasonModel season = getSingleSeason();

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        seasonRepository.deleteAll();
    }

    @AfterAll
    public void cleanUp() {
        seasonRepository.deleteAll();
    }

    @Test
    public void shouldCreateSeason() throws Exception {
        SeasonModel seasonToBeSaved = getNewSeason();

        mockMvc.perform(post("/api/v1/seasons")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(seasonToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(seasonToBeSaved.getTitle()))
                .andExpect(jsonPath("$.description").value(seasonToBeSaved.getDescription()))
                .andExpect(jsonPath("$.duration_total").value(String.valueOf(seasonToBeSaved.getDurationTotal())))
                .andExpect(jsonPath("$.season_num").value(String.valueOf(seasonToBeSaved.getSeasonNum())))
                .andExpect(jsonPath("$.release_date").value(seasonToBeSaved.getReleaseDate().toString()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedCreateSeason() throws Exception {
        SeasonModel seasonToBeSaved = getNewSeason();

        mockMvc.perform(post("/api/v1/seasons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(seasonToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenCreateSeason() throws Exception {
        SeasonModel seasonToBeSaved = getNewSeason();

        mockMvc.perform(post("/api/v1/seasons")
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(seasonToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnSeasonByUUID() throws Exception {
        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(get("/api/v1/seasons/" + season.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(season.getUuid().toString()))
                .andExpect(jsonPath("$.href").value(season.getHref()))
                .andExpect(jsonPath("$.title").value(season.getTitle()))
                .andExpect(jsonPath("$.description").value(season.getDescription()))
                .andExpect(jsonPath("$.duration_total").value(String.valueOf(season.getDurationTotal())))
                .andExpect(jsonPath("$.season_num").value(String.valueOf(season.getSeasonNum())))
                .andExpect(jsonPath("$.release_date").value(season.getReleaseDate().toString()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundSeason() throws Exception {
        mockMvc.perform(get("/api/v1/seasons/" + season.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfSeasons() throws Exception {
        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(get("/api/v1/seasons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyListOfSeasons() throws Exception {
        mockMvc.perform(get("/api/v1/seasons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnListOfSeasonsWithFilter() throws Exception {
        SeasonModel season = getSeasons().get(1);

        seasonRepository.saveAll(getSeasons());

        // FIXME: SeasonFilter will only read if field is camelcase (seasonNum).
        mockMvc.perform(get("/api/v1/seasons")
                        .param("season_num", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(season.getUuid().toString()))
                .andExpect(jsonPath("$[0].href").value(season.getHref()))
                .andExpect(jsonPath("$[0].title").value(season.getTitle()))
                .andExpect(jsonPath("$[0].description").value(season.getDescription()))
                .andExpect(jsonPath("$[0].duration_total").value(String.valueOf(season.getDurationTotal())))
                .andExpect(jsonPath("$[0].season_num").value(String.valueOf(season.getSeasonNum())))
                .andExpect(jsonPath("$[0].release_date").value(season.getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnListOfSeasonsWithSortFilter() throws Exception {
        List<SeasonModel> reversedSeasons = new ArrayList<>(getSeasons());
        Collections.reverse(reversedSeasons);

        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(get("/api/v1/seasons")
                        .param("sort", "DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(reversedSeasons.get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(reversedSeasons.get(0).getUuid().toString()));
    }

    @Test
    public void shouldReturnListOfSeasonsWithOrderFilter() throws Exception {
        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(get("/api/v1/seasons")
                        .param("order", "title")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(getSeasons().get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(getSeasons().get(0).getUuid().toString()));
    }

    @Test
    public void shouldUpdateSeason() throws Exception {
        SeasonModel seasonToBeUpdated = new SeasonModel();
        seasonToBeUpdated.setTitle("Moa");

        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(patch("/api/v1/seasons/" + season.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(seasonToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundUpdateSeason() throws Exception {
        SeasonModel seasonToBeUpdated = new SeasonModel();
        seasonToBeUpdated.setTitle("Moa");

        mockMvc.perform(patch("/api/v1/seasons/" + season.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(seasonToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedUpdateSeason() throws Exception {
        SeasonModel seasonToBeUpdated = new SeasonModel();
        seasonToBeUpdated.setTitle("Moa");

        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(patch("/api/v1/seasons/" + season.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(seasonToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenUpdateSeason() throws Exception {
        SeasonModel seasonToBeUpdated = new SeasonModel();
        seasonToBeUpdated.setTitle("Moa");

        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(patch("/api/v1/seasons/" + season.getUuid())
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(seasonToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeleteSeason() throws Exception {
        seasonRepository.saveAll(getSeasons());

        mockMvc.perform(delete("/api/v1/seasons/" + season.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteSeason() throws Exception {
        mockMvc.perform(delete("/api/v1/seasons/" + season.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteSeason() throws Exception {
        mockMvc.perform(delete("/api/v1/seasons/" + season.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenDeleteSeason() throws Exception {
        mockMvc.perform(delete("/api/v1/seasons/" + season.getUuid())
                        .with(getDevAuth())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
