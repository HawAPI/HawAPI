package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.dto.GameDTO;
import com.lucasjosino.hawapi.models.dto.translation.GameTranslationDTO;
import com.lucasjosino.hawapi.models.translations.GameTranslation;
import com.lucasjosino.hawapi.repositories.GameRepository;
import com.lucasjosino.hawapi.repositories.translation.GameTranslationRepository;
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
class GameIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/games/";

    private static final ModelMapper mapper = new ModelMapper();

    private GameDTO gameDTO;

    private GameTranslationDTO translationDTO;

    @Autowired
    private GameRepository repository;

    @Autowired
    private GameTranslationRepository translationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        gameDTO = new GameDTO();
        gameDTO.setUuid(UUID.randomUUID());
        gameDTO.setHref(URL + "/" + gameDTO.getUuid());
        gameDTO.setReleaseDate(LocalDate.now());
        gameDTO.setWebsite("https://example.com");
        gameDTO.setPlaytime(210574565);
        gameDTO.setAgeRating("100+");
        gameDTO.setStores(Arrays.asList("https://store.example.com", "https://store.example.com"));
        gameDTO.setModes(Arrays.asList("Single Player", "Multi Player"));
        gameDTO.setPublishers(Arrays.asList("Lorem", "Ipsum"));
        gameDTO.setDevelopers(Arrays.asList("Lorem", "Ipsum"));
        gameDTO.setPlatforms(Arrays.asList("Android", "iOS"));
        gameDTO.setTags(Arrays.asList("horror", "suspense"));
        gameDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        gameDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        gameDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        gameDTO.setCreatedAt(LocalDateTime.now());
        gameDTO.setUpdatedAt(LocalDateTime.now());
        gameDTO.setLanguage("en-US");
        gameDTO.setName("Lorem Ipsum");
        gameDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        gameDTO.setTrailer("https://youtube.com/watch?v=1");
        gameDTO.setGenres(Arrays.asList("Lorem", "Ipsum"));

        translationDTO = new GameTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setName("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translationDTO.setTrailer("https://youtube.com/watch?v=1");
        translationDTO.setGenres(Arrays.asList("Lorem", "Ipsum"));

        GameModel gameModel = mapper.map(gameDTO, GameModel.class);
        repository.save(gameModel);

        GameTranslation gameTranslation = mapper.map(translationDTO, GameTranslation.class);
        gameTranslation.setGameUuid(gameModel.getUuid());
        translationRepository.save(gameTranslation);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllGames() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "10"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnAllGamesWithPortugueseLanguage() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "10"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnAllGameTranslations() throws Exception {
        mockMvc.perform(get(URL + "/" + gameDTO.getUuid() + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(translationDTO.getName()))
                .andExpect(jsonPath("$[0].description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$[0].language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoGameLanguageFoundShouldThrowItemNotFoundExceptionOnAllGamesWithPortugueseLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + gameDTO.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllGames() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "10"))
                .andExpect(header().string("X-Pagination-Page-Total", "0"))
                .andExpect(header().string("X-Pagination-Item-Total", "0"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnRandomGame() throws Exception {
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(gameDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(gameDTO.getHref()))
                .andExpect(jsonPath("$.name").value(gameDTO.getName()))
                .andExpect(jsonPath("$.description").value(gameDTO.getDescription()))
                .andExpect(jsonPath("$.trailer").value(gameDTO.getTrailer()))
                .andExpect(jsonPath("$.language").value(gameDTO.getLanguage()))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(gameDTO.getReleaseDate())))
                .andExpect(jsonPath("$.website").value(gameDTO.getWebsite()))
                .andExpect(jsonPath("$.playtime").value(String.valueOf(gameDTO.getPlaytime())))
                .andExpect(jsonPath("$.age_rating").value(gameDTO.getAgeRating()))
                .andExpect(jsonPath("$.stores").isNotEmpty())
                .andExpect(jsonPath("$.modes").isNotEmpty())
                .andExpect(jsonPath("$.publishers").isNotEmpty())
                .andExpect(jsonPath("$.developers").isNotEmpty())
                .andExpect(jsonPath("$.platforms").isNotEmpty())
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(gameDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnRandomGameTranslation() throws Exception {
        mockMvc.perform(get(URL + "/" + gameDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.name").value(translationDTO.getName()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomGameTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + gameDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnRandomGame() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnGameByUUID() throws Exception {
        mockMvc.perform(get(URL + "/" + gameDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(gameDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(gameDTO.getHref()))
                .andExpect(jsonPath("$.name").value(gameDTO.getName()))
                .andExpect(jsonPath("$.description").value(gameDTO.getDescription()))
                .andExpect(jsonPath("$.trailer").value(gameDTO.getTrailer()))
                .andExpect(jsonPath("$.language").value(gameDTO.getLanguage()))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(gameDTO.getReleaseDate())))
                .andExpect(jsonPath("$.website").value(gameDTO.getWebsite()))
                .andExpect(jsonPath("$.playtime").value(String.valueOf(gameDTO.getPlaytime())))
                .andExpect(jsonPath("$.age_rating").value(gameDTO.getAgeRating()))
                .andExpect(jsonPath("$.stores").isNotEmpty())
                .andExpect(jsonPath("$.modes").isNotEmpty())
                .andExpect(jsonPath("$.publishers").isNotEmpty())
                .andExpect(jsonPath("$.developers").isNotEmpty())
                .andExpect(jsonPath("$.platforms").isNotEmpty())
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(gameDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnGameTranslationByUUIDAndLanguage() throws Exception {
        mockMvc.perform(get(URL + "/" + gameDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.name").value(translationDTO.getName()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnGameByUUID() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/" + gameDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoGameTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + gameDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveGame() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(gameDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.name").value(gameDTO.getName()))
                .andExpect(jsonPath("$.description").value(gameDTO.getDescription()))
                .andExpect(jsonPath("$.trailer").value(gameDTO.getTrailer()))
                .andExpect(jsonPath("$.language").value(gameDTO.getLanguage()))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(gameDTO.getReleaseDate())))
                .andExpect(jsonPath("$.website").value(gameDTO.getWebsite()))
                .andExpect(jsonPath("$.playtime").value(String.valueOf(gameDTO.getPlaytime())))
                .andExpect(jsonPath("$.age_rating").value(gameDTO.getAgeRating()))
                .andExpect(jsonPath("$.stores").isNotEmpty())
                .andExpect(jsonPath("$.modes").isNotEmpty())
                .andExpect(jsonPath("$.publishers").isNotEmpty())
                .andExpect(jsonPath("$.developers").isNotEmpty())
                .andExpect(jsonPath("$.platforms").isNotEmpty())
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(gameDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldSaveGameTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(post(URL + "/" + gameDTO.getUuid() + "/translations")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.name").value(translationDTO.getName()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveGame() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(gameDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveGameTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + gameDTO.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveGame() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(gameDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveGameTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + gameDTO.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveGame() throws Exception {
        gameDTO.setReleaseDate(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(gameDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'release_date' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveGameTranslation() throws Exception {
        translationDTO.setName(null);

        String url = URL + gameDTO.getUuid() + "/translations";
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
                .andExpect(jsonPath("$.message").value("Field 'name' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(url));
    }


    @Test
    void shouldUpdateGame() throws Exception {
        GameDTO patch = new GameDTO();
        patch.setReleaseDate(LocalDate.now());

        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(patch.getReleaseDate())));
    }

    @Test
    void shouldUpdateGameTranslation() throws Exception {
        GameTranslationDTO patch = new GameTranslationDTO();
        patch.setName("Lorem");

        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.name").value(String.valueOf(patch.getName())));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateGame() throws Exception {
        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateGameTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateGame() throws Exception {
        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateGameTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateGame() throws Exception {
        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateGameTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGame() throws Exception {
        repository.deleteAll();

        GameDTO patch = new GameDTO();
        patch.setReleaseDate(LocalDate.now());

        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGameTranslation() throws Exception {
        translationRepository.deleteAll();

        GameTranslationDTO patch = new GameTranslationDTO();
        patch.setName("Ipsum");

        mockMvc.perform(patch(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteGame() throws Exception {
        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteGameTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteGame() throws Exception {
        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteGameTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteGame() throws Exception {
        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteGameTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGameOnDeleteGame() throws Exception {
        repository.deleteAll();

        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGameOnDeleteGameTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(delete(URL + "/" + gameDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}