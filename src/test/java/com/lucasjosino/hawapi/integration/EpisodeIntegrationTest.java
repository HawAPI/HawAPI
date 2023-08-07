package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import com.lucasjosino.hawapi.models.dto.translation.EpisodeTranslationDTO;
import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import com.lucasjosino.hawapi.repositories.translation.EpisodeTranslationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTestConfig
class EpisodeIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/episodes/";

    private static final ModelMapper mapper = new ModelMapper();

    private EpisodeDTO episodeDTO;

    private EpisodeTranslationDTO translationDTO;

    @Autowired
    private EpisodeRepository repository;

    @Autowired
    private EpisodeTranslationRepository translationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        episodeDTO = new EpisodeDTO();
        episodeDTO.setUuid(UUID.randomUUID());
        episodeDTO.setHref(URL + "/" + episodeDTO.getUuid());
        episodeDTO.setDuration(12482342);
        episodeDTO.setEpisodeNum((byte) 2);
        episodeDTO.setNextEpisode("/api/v1/episodes/3");
        episodeDTO.setPrevEpisode("/api/v1/episodes/1");
        episodeDTO.setSeason("/api/v1/seasons/1");
        episodeDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        episodeDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        episodeDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        episodeDTO.setCreatedAt(LocalDateTime.now());
        episodeDTO.setUpdatedAt(LocalDateTime.now());
        episodeDTO.setLanguage("en-US");
        episodeDTO.setTitle("Lorem Ipsum");
        episodeDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translationDTO = new EpisodeTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setTitle("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        EpisodeModel episodeModel = mapper.map(episodeDTO, EpisodeModel.class);
        repository.save(episodeModel);

        EpisodeTranslation episodeTranslation = mapper.map(translationDTO, EpisodeTranslation.class);
        episodeTranslation.setEpisodeUuid(episodeModel.getUuid());
        translationRepository.save(episodeTranslation);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllEpisodes() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "1"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnAllEpisodesWithPortugueseLanguage() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "1"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnAllEpisodeTranslations() throws Exception {
        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid() + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(translationDTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$[0].language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoEpisodeLanguageFoundShouldThrowItemNotFoundExceptionOnAllEpisodesWithPortugueseLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllEpisodes() throws Exception {
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
    void shouldReturnRandomEpisode() throws Exception {
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(episodeDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(episodeDTO.getHref()))
                .andExpect(jsonPath("$.title").value(episodeDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(episodeDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(episodeDTO.getLanguage()))
                .andExpect(jsonPath("$.duration").value(String.valueOf(episodeDTO.getDuration())))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episodeDTO.getEpisodeNum())))
                .andExpect(jsonPath("$.next_episode").value(episodeDTO.getNextEpisode()))
                .andExpect(jsonPath("$.prev_episode").value(episodeDTO.getPrevEpisode()))
                .andExpect(jsonPath("$.season").value(episodeDTO.getSeason()))
                .andExpect(jsonPath("$.thumbnail").value(episodeDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnRandomEpisodeTranslation() throws Exception {
        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.title").value(translationDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomEpisodeTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnRandomEpisode() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEpisodeByUUID() throws Exception {
        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(episodeDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(episodeDTO.getHref()))
                .andExpect(jsonPath("$.title").value(episodeDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(episodeDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(episodeDTO.getLanguage()))
                .andExpect(jsonPath("$.duration").value(String.valueOf(episodeDTO.getDuration())))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episodeDTO.getEpisodeNum())))
                .andExpect(jsonPath("$.next_episode").value(episodeDTO.getNextEpisode()))
                .andExpect(jsonPath("$.prev_episode").value(episodeDTO.getPrevEpisode()))
                .andExpect(jsonPath("$.season").value(episodeDTO.getSeason()))
                .andExpect(jsonPath("$.thumbnail").value(episodeDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnEpisodeTranslationByUUIDAndLanguage() throws Exception {
        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(translationDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnEpisodeByUUID() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoEpisodeTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + episodeDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveEpisode() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episodeDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.title").value(episodeDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(episodeDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(episodeDTO.getLanguage()))
                .andExpect(jsonPath("$.duration").value(String.valueOf(episodeDTO.getDuration())))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episodeDTO.getEpisodeNum())))
                .andExpect(jsonPath("$.next_episode").value(episodeDTO.getNextEpisode()))
                .andExpect(jsonPath("$.prev_episode").value(episodeDTO.getPrevEpisode()))
                .andExpect(jsonPath("$.season").value(episodeDTO.getSeason()))
                .andExpect(jsonPath("$.thumbnail").value(episodeDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldSaveEpisodeTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(post(URL + "/" + episodeDTO.getUuid() + "/translations")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(translationDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveEpisode() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episodeDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveEpisodeTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + episodeDTO.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveEpisode() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episodeDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveEpisodeTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + episodeDTO.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveEpisode() throws Exception {
        episodeDTO.setEpisodeNum(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episodeDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'episode_num' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveEpisodeTranslation() throws Exception {
        translationDTO.setTitle(null);

        String url = URL + episodeDTO.getUuid() + "/translations";
        mockMvc.perform(post(url)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'title' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(url));
    }


    @Test
    void shouldUpdateEpisode() throws Exception {
        EpisodeDTO patch = new EpisodeDTO();
        patch.setEpisodeNum((byte) 5);

        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(patch.getEpisodeNum())));
    }

    @Test
    void shouldUpdateEpisodeTranslation() throws Exception {
        EpisodeTranslationDTO patch = new EpisodeTranslationDTO();
        patch.setTitle("Lorem");

        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(String.valueOf(patch.getTitle())));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateEpisode() throws Exception {
        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateEpisodeTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateEpisode() throws Exception {
        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateEpisodeTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateEpisode() throws Exception {
        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateEpisodeTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisode() throws Exception {
        repository.deleteAll();

        EpisodeDTO patch = new EpisodeDTO();
        patch.setEpisodeNum((byte) 5);

        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisodeTranslation() throws Exception {
        translationRepository.deleteAll();

        EpisodeTranslationDTO patch = new EpisodeTranslationDTO();
        patch.setTitle("Ipsum");

        mockMvc.perform(patch(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteEpisode() throws Exception {
        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteEpisodeTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteEpisode() throws Exception {
        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteEpisodeTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteEpisode() throws Exception {
        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteEpisodeTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisodeOnDeleteEpisode() throws Exception {
        repository.deleteAll();

        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisodeOnDeleteEpisodeTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(delete(URL + "/" + episodeDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}