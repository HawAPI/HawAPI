package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.dto.SeasonDTO;
import com.lucasjosino.hawapi.models.dto.translation.SeasonTranslationDTO;
import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import com.lucasjosino.hawapi.repositories.translation.SeasonTranslationRepository;
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
class SeasonIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/seasons/";

    private static final ModelMapper mapper = new ModelMapper();

    private SeasonDTO seasonDTO;

    private SeasonTranslationDTO translationDTO;

    @Autowired
    private SeasonRepository repository;

    @Autowired
    private SeasonTranslationRepository translationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        seasonDTO = new SeasonDTO();
        seasonDTO.setUuid(UUID.randomUUID());
        seasonDTO.setHref(URL + "/" + seasonDTO.getUuid());
        seasonDTO.setDurationTotal(215398753);
        seasonDTO.setSeasonNum((byte) 2);
        seasonDTO.setReleaseDate(LocalDate.now());
        seasonDTO.setNextSeason("/api/v1/seasons/3");
        seasonDTO.setPrevSeason("/api/v1/seasons/1");
        seasonDTO.setEpisodes(Arrays.asList("/api/v1/episodes/1", "/api/v1/episodes/2", "/api/v1/episodes/3"));
        seasonDTO.setSoundtracks(Arrays.asList("/api/v1/soundtracks/1",
                "/api/v1/soundtracks/2",
                "/api/v1/soundtracks/3"
        ));
        seasonDTO.setBudget(218459);
        seasonDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        seasonDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        seasonDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        seasonDTO.setCreatedAt(LocalDateTime.now());
        seasonDTO.setUpdatedAt(LocalDateTime.now());
        seasonDTO.setLanguage("en-US");
        seasonDTO.setTitle("Lorem Ipsum");
        seasonDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        seasonDTO.setGenres(Arrays.asList("gen1", "gen2", "gen3"));
        seasonDTO.setTrailers(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));

        translationDTO = new SeasonTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setTitle("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translationDTO.setGenres(Arrays.asList("gen1", "gen2", "gen3"));
        translationDTO.setTrailers(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));

        SeasonModel seasonModel = mapper.map(seasonDTO, SeasonModel.class);
        repository.save(seasonModel);

        SeasonTranslation seasonTranslation = mapper.map(translationDTO, SeasonTranslation.class);
        seasonTranslation.setSeasonUuid(seasonModel.getUuid());
        translationRepository.save(seasonTranslation);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllSeasons() throws Exception {
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
    void shouldReturnAllSeasonsWithPortugueseLanguage() throws Exception {
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
    void shouldReturnAllSeasonTranslations() throws Exception {
        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid() + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(translationDTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$[0].language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoSeasonLanguageFoundShouldThrowItemNotFoundExceptionOnAllSeasonsWithPortugueseLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllSeasons() throws Exception {
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
    void shouldReturnRandomSeason() throws Exception {
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(seasonDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(seasonDTO.getHref()))
                .andExpect(jsonPath("$.title").value(seasonDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(seasonDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(seasonDTO.getLanguage()))
                .andExpect(jsonPath("$.thumbnail").value(seasonDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnRandomSeasonTranslation() throws Exception {
        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.title").value(translationDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomSeasonTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnRandomSeason() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSeasonByUUID() throws Exception {
        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(seasonDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(seasonDTO.getHref()))
                .andExpect(jsonPath("$.title").value(seasonDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(seasonDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(seasonDTO.getLanguage()))
                .andExpect(jsonPath("$.thumbnail").value(seasonDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnSeasonTranslationByUUIDAndLanguage() throws Exception {
        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(translationDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnSeasonByUUID() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoSeasonTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + seasonDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveSeason() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(seasonDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.title").value(seasonDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(seasonDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(seasonDTO.getLanguage()))
                .andExpect(jsonPath("$.thumbnail").value(seasonDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldSaveSeasonTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(post(URL + "/" + seasonDTO.getUuid() + "/translations")
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
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveSeason() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(seasonDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveSeasonTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + seasonDTO.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveSeason() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(seasonDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveSeasonTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + seasonDTO.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveSeason() throws Exception {
        seasonDTO.setTitle(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(seasonDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'title' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveSeasonTranslation() throws Exception {
        translationDTO.setTitle(null);

        String url = URL + seasonDTO.getUuid() + "/translations";
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
    void shouldUpdateSeason() throws Exception {
        SeasonDTO patch = new SeasonDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image2.jpg");

        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.thumbnail").value(String.valueOf(patch.getThumbnail())));
    }

    @Test
    void shouldUpdateSeasonTranslation() throws Exception {
        SeasonTranslationDTO patch = new SeasonTranslationDTO();
        patch.setTitle("Lorem");

        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
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
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateSeason() throws Exception {
        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateSeasonTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateSeason() throws Exception {
        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateSeasonTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateSeason() throws Exception {
        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateSeasonTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeason() throws Exception {
        repository.deleteAll();

        SeasonDTO patch = new SeasonDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image2.jpg");

        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeasonTranslation() throws Exception {
        translationRepository.deleteAll();

        SeasonTranslationDTO patch = new SeasonTranslationDTO();
        patch.setTitle("Ipsum");

        mockMvc.perform(patch(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteSeason() throws Exception {
        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteSeasonTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteSeason() throws Exception {
        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteSeasonTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteSeason() throws Exception {
        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteSeasonTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeasonOnDeleteSeason() throws Exception {
        repository.deleteAll();

        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeasonOnDeleteSeasonTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(delete(URL + "/" + seasonDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}