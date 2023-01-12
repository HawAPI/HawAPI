package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
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
public class SoundtrackControllerIntTest extends DatabaseContainerInitializer {

    private static final SoundtrackModel soundtrack = getSingleSoundtrack();

    @Autowired
    private SoundtrackRepository soundtrackRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        soundtrackRepository.deleteAll();
    }

    @AfterAll
    public void cleanUp() {
        soundtrackRepository.deleteAll();
    }

    @Test
    public void shouldCreateSoundtrack() throws Exception {
        SoundtrackModel soundtrackToBeSaved = getNewSoundtrack();

        mockMvc.perform(post("/api/v1/soundtracks")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(soundtrackToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(soundtrackToBeSaved.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrackToBeSaved.getArtist()))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrackToBeSaved.getReleaseDate())))
                .andExpect(jsonPath("$.urls").value(hasSize(1)))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedCreateSoundtrack() throws Exception {
        SoundtrackModel soundtrackToBeSaved = getNewSoundtrack();

        mockMvc.perform(post("/api/v1/soundtracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(soundtrackToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenCreateSoundtrack() throws Exception {
        SoundtrackModel soundtrackToBeSaved = getNewSoundtrack();

        mockMvc.perform(post("/api/v1/soundtracks")
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(soundtrackToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnSoundtrackByUUID() throws Exception {
        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(get("/api/v1/soundtracks/" + soundtrack.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(soundtrack.getUuid().toString()))
                .andExpect(jsonPath("$.href").value(soundtrack.getHref()))
                .andExpect(jsonPath("$.name").value(soundtrack.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrack.getArtist()))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrack.getReleaseDate())))
                .andExpect(jsonPath("$.urls").value(hasSize(1)))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundSoundtrack() throws Exception {
        mockMvc.perform(get("/api/v1/soundtracks/" + soundtrack.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfSoundtracks() throws Exception {
        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(get("/api/v1/soundtracks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyListOfSoundtracks() throws Exception {
        mockMvc.perform(get("/api/v1/soundtracks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnListOfSoundtracksWithFilter() throws Exception {
        SoundtrackModel soundtrack = getSoundtracks().get(1);

        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(get("/api/v1/soundtracks")
                        .param("name", "Ipsum")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(soundtrack.getUuid().toString()))
                .andExpect(jsonPath("$[0].href").value(soundtrack.getHref()))
                .andExpect(jsonPath("$[0].name").value(soundtrack.getName()))
                .andExpect(jsonPath("$[0].artist").value(soundtrack.getArtist()))
                .andExpect(jsonPath("$[0].release_date").value(String.valueOf(soundtrack.getReleaseDate())))
                .andExpect(jsonPath("$[0].urls").value(hasSize(1)))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnListOfSoundtracksWithSortFilter() throws Exception {
        List<SoundtrackModel> reversedSoundtracks = new ArrayList<>(getSoundtracks());
        Collections.reverse(reversedSoundtracks);

        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(get("/api/v1/soundtracks")
                        .param("sort", "DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(reversedSoundtracks.get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(reversedSoundtracks.get(0).getUuid().toString()));
    }

    @Test
    public void shouldReturnListOfSoundtracksWithOrderFilter() throws Exception {
        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(get("/api/v1/soundtracks")
                        .param("order", "name")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(getSoundtracks().get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(getSoundtracks().get(0).getUuid().toString()));
    }

    @Test
    public void shouldUpdateSoundtrack() throws Exception {
        SoundtrackModel soundtrackToBeUpdated = new SoundtrackModel();
        soundtrackToBeUpdated.setName("Moa");

        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(patch("/api/v1/soundtracks/" + soundtrack.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(soundtrackToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundUpdateSoundtrack() throws Exception {
        SoundtrackModel soundtrackToBeUpdated = new SoundtrackModel();
        soundtrackToBeUpdated.setName("Moa");

        mockMvc.perform(patch("/api/v1/soundtracks/" + soundtrack.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(soundtrackToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedUpdateSoundtrack() throws Exception {
        SoundtrackModel soundtrackToBeUpdated = new SoundtrackModel();
        soundtrackToBeUpdated.setName("Moa");

        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(patch("/api/v1/soundtracks/" + soundtrack.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(soundtrackToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenUpdateSoundtrack() throws Exception {
        SoundtrackModel soundtrackToBeUpdated = new SoundtrackModel();
        soundtrackToBeUpdated.setName("Moa");

        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(patch("/api/v1/soundtracks/" + soundtrack.getUuid())
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(soundtrackToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeleteSoundtrack() throws Exception {
        soundtrackRepository.saveAll(getSoundtracks());

        mockMvc.perform(delete("/api/v1/soundtracks/" + soundtrack.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete("/api/v1/soundtracks/" + soundtrack.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete("/api/v1/soundtracks/" + soundtrack.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete("/api/v1/soundtracks/" + soundtrack.getUuid())
                        .with(getDevAuth())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
