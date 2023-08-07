package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTestConfig
class SoundtrackIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/soundtracks/";

    private static final ModelMapper mapper = new ModelMapper();

    private SoundtrackDTO soundtrackDTO;

    @Autowired
    private SoundtrackRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        soundtrackDTO = new SoundtrackDTO();
        soundtrackDTO.setUuid(UUID.randomUUID());
        soundtrackDTO.setHref(URL + "/" + soundtrackDTO.getUuid());
        soundtrackDTO.setName("Lorem");
        soundtrackDTO.setArtist("Ipsum");
        soundtrackDTO.setAlbum("Lorem Ipsum");
        soundtrackDTO.setUrls(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));
        soundtrackDTO.setDuration(158351809);
        soundtrackDTO.setReleaseDate(LocalDate.now());
        soundtrackDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        soundtrackDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        soundtrackDTO.setCreatedAt(LocalDateTime.now());
        soundtrackDTO.setUpdatedAt(LocalDateTime.now());

        SoundtrackModel soundtrackModel = mapper.map(soundtrackDTO, SoundtrackModel.class);
        repository.save(soundtrackModel);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllSoundtracks() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "1"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyList() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "0"))
                .andExpect(header().string("X-Pagination-Page-Total", "0"))
                .andExpect(header().string("X-Pagination-Item-Total", "0"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void shouldReturnRandomSoundtrack() throws Exception {
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(soundtrackDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(soundtrackDTO.getHref()))
                .andExpect(jsonPath("$.name").value(soundtrackDTO.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrackDTO.getArtist()))
                .andExpect(jsonPath("$.album").value(soundtrackDTO.getAlbum()))
                .andExpect(jsonPath("$.urls").isNotEmpty())
                .andExpect(jsonPath("$.duration").value(String.valueOf(soundtrackDTO.getDuration())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrackDTO.getReleaseDate())))
                .andExpect(jsonPath("$.thumbnail").value(soundtrackDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnRandomSoundtrack() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSoundtrackByUUID() throws Exception {
        mockMvc.perform(get(URL + "/" + soundtrackDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(soundtrackDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(soundtrackDTO.getHref()))
                .andExpect(jsonPath("$.name").value(soundtrackDTO.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrackDTO.getArtist()))
                .andExpect(jsonPath("$.album").value(soundtrackDTO.getAlbum()))
                .andExpect(jsonPath("$.urls").isNotEmpty())
                .andExpect(jsonPath("$.duration").value(String.valueOf(soundtrackDTO.getDuration())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrackDTO.getReleaseDate())))
                .andExpect(jsonPath("$.thumbnail").value(soundtrackDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnSoundtrackByUUID() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/" + soundtrackDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveSoundtrack() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrackDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.name").value(soundtrackDTO.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrackDTO.getArtist()))
                .andExpect(jsonPath("$.album").value(soundtrackDTO.getAlbum()))
                .andExpect(jsonPath("$.urls").isNotEmpty())
                .andExpect(jsonPath("$.duration").value(String.valueOf(soundtrackDTO.getDuration())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrackDTO.getReleaseDate())))
                .andExpect(jsonPath("$.thumbnail").value(soundtrackDTO.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveSoundtrack() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrackDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveSoundtrack() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrackDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveSoundtrack() throws Exception {
        soundtrackDTO.setName(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrackDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'name' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void shouldUpdateSoundtrack() throws Exception {
        SoundtrackDTO patch = new SoundtrackDTO();
        patch.setDuration(1247149);

        mockMvc.perform(patch(URL + "/" + soundtrackDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.duration").value(String.valueOf(patch.getDuration())));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateSoundtrack() throws Exception {
        mockMvc.perform(patch(URL + "/" + soundtrackDTO.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateSoundtrack() throws Exception {
        mockMvc.perform(patch(URL + "/" + soundtrackDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateSoundtrack() throws Exception {
        mockMvc.perform(patch(URL + "/" + soundtrackDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnUpdateSoundtrack() throws Exception {
        repository.deleteAll();

        SoundtrackDTO patch = new SoundtrackDTO();
        patch.setDuration(1247149);

        mockMvc.perform(patch(URL + "/" + soundtrackDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete(URL + "/" + soundtrackDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete(URL + "/" + soundtrackDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete(URL + "/" + soundtrackDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnUpdateSoundtrackOnDeleteSoundtrack() throws Exception {
        repository.deleteAll();

        mockMvc.perform(delete(URL + "/" + soundtrackDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}