package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.GameDTO;
import com.lucasjosino.hawapi.models.dto.translation.GameTranslationDTO;
import com.lucasjosino.hawapi.services.impl.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.lucasjosino.hawapi.utils.TestUtils.buildHeaders;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = GameController.class)
@ContextConfiguration(classes = {GameController.class, ControllerAdvisor.class, SecurityConfig.class})
class GameControllerUnitTest {

    private static final String URL = "/api/v1/games";

    private GameDTO game;

    private GameTranslationDTO translation;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        game = new GameDTO();
        game.setUuid(UUID.randomUUID());
        game.setHref(URL + "/" + game.getUuid());
        game.setLanguages(Collections.singletonList("Lorem"));
        game.setReleaseDate(LocalDate.now());
        game.setWebsite("https://example.com");
        game.setPlaytime(210574565);
        game.setAgeRating("100+");
        game.setStores(Arrays.asList("https://store.example.com", "https://store.example.com"));
        game.setModes(Arrays.asList("Single Player", "Multi Player"));
        game.setPublishers(Arrays.asList("Lorem", "Ipsum"));
        game.setDevelopers(Arrays.asList("Lorem", "Ipsum"));
        game.setPlatforms(Arrays.asList("Android", "iOS"));
        game.setGenres(Arrays.asList("Lorem", "Ipsum"));
        game.setTags(Arrays.asList("horror", "suspense"));
        game.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        game.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        game.setSources(Arrays.asList("https://example.com", "https://example.com"));
        game.setCreatedAt(LocalDateTime.now());
        game.setUpdatedAt(LocalDateTime.now());
        game.setLanguage("en-US");
        game.setName("Lorem Ipsum");
        game.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        game.setTrailer("https://youtube.com/watch?v=1");

        translation = new GameTranslationDTO();
        translation.setLanguage("en-US");
        translation.setName("Lorem Ipsum");
        translation.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translation.setTrailer("https://youtube.com/watch?v=1");
    }

    @Test
    void shouldReturnAllGames() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, null);

        when(service.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class))).thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(game));

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

        verify(service, times(1)).findAllUUIDs(any(Pageable.class));
        verify(responseUtils, times(1)).getHeaders(any(), any(Pageable.class), nullable(String.class));
        verify(service, times(1)).findAll(anyMap(), anyList());
    }

    @Test
    void shouldReturnAllGamesWithPortugueseLanguage() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, "pt-BR");

        when(service.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class))).thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(game));

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "1"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(service, times(1)).findAllUUIDs(any(Pageable.class));
        verify(responseUtils, times(1)).getHeaders(any(), any(Pageable.class), nullable(String.class));
        verify(service, times(1)).findAll(anyMap(), anyList());
    }

    @Test
    void shouldReturnAllGameTranslations() throws Exception {
        when(service.findAllTranslationsBy(any(UUID.class))).thenReturn(Collections.singletonList(translation));

        mockMvc.perform(get(URL + "/" + game.getUuid() + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(translation.getName()))
                .andExpect(jsonPath("$[0].description").value(translation.getDescription()))
                .andExpect(jsonPath("$[0].language").value(translation.getLanguage()));

        verify(service, times(1)).findAllTranslationsBy(any(UUID.class));
    }

    @Test
    void whenNoGameLanguageFoundShouldThrowItemNotFoundExceptionOnAllGamesWithPortugueseLanguage() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + game.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllGames() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.emptyList();
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 0
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, null);

        when(service.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class))).thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "1"))
                .andExpect(header().string("X-Pagination-Page-Total", "0"))
                .andExpect(header().string("X-Pagination-Item-Total", "0"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).findAllUUIDs(any(Pageable.class));
        verify(responseUtils, times(1)).getHeaders(any(), any(Pageable.class), nullable(String.class));
        verify(service, times(1)).findAll(anyMap(), anyList());
    }

    @Test
    void shouldReturnRandomGame() throws Exception {
        when(service.findRandom(nullable(String.class))).thenReturn(game);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(game.getUuid())))
                .andExpect(jsonPath("$.href").value(game.getHref()))
                .andExpect(jsonPath("$.name").value(game.getName()))
                .andExpect(jsonPath("$.description").value(game.getDescription()))
                .andExpect(jsonPath("$.trailer").value(game.getTrailer()))
                .andExpect(jsonPath("$.language").value(game.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.release_date").value(String.valueOf(game.getReleaseDate())))
                .andExpect(jsonPath("$.website").value(game.getWebsite()))
                .andExpect(jsonPath("$.playtime").value(String.valueOf(game.getPlaytime())))
                .andExpect(jsonPath("$.age_rating").value(game.getAgeRating()))
                .andExpect(jsonPath("$.stores").isNotEmpty())
                .andExpect(jsonPath("$.modes").isNotEmpty())
                .andExpect(jsonPath("$.publishers").isNotEmpty())
                .andExpect(jsonPath("$.developers").isNotEmpty())
                .andExpect(jsonPath("$.platforms").isNotEmpty())
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(game.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnRandomGameTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("en-US");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findRandomTranslation(any(UUID.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + game.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.name").value(translation.getName()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()))
                .andExpect(jsonPath("$.trailer").value(translation.getTrailer()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomGameTranslation() throws Exception {
        when(service.findRandomTranslation(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + game.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnRandomGame() throws Exception {
        when(service.findRandom(nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnGameByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenReturn(game);

        mockMvc.perform(get(URL + "/" + game.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(game.getUuid())))
                .andExpect(jsonPath("$.href").value(game.getHref()))
                .andExpect(jsonPath("$.name").value(game.getName()))
                .andExpect(jsonPath("$.description").value(game.getDescription()))
                .andExpect(jsonPath("$.trailer").value(game.getTrailer()))
                .andExpect(jsonPath("$.language").value(game.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.release_date").value(String.valueOf(game.getReleaseDate())))
                .andExpect(jsonPath("$.website").value(game.getWebsite()))
                .andExpect(jsonPath("$.playtime").value(String.valueOf(game.getPlaytime())))
                .andExpect(jsonPath("$.age_rating").value(game.getAgeRating()))
                .andExpect(jsonPath("$.stores").isNotEmpty())
                .andExpect(jsonPath("$.modes").isNotEmpty())
                .andExpect(jsonPath("$.publishers").isNotEmpty())
                .andExpect(jsonPath("$.developers").isNotEmpty())
                .andExpect(jsonPath("$.platforms").isNotEmpty())
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(game.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldReturnGameTranslationByUUIDAndLanguage() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + game.getUuid() + "/translations/pt-BR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.name").value(translation.getName()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()))
                .andExpect(jsonPath("$.trailer").value(translation.getTrailer()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnGameByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + game.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoGameTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + game.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldSaveGame() throws Exception {
        when(service.save(any(GameDTO.class))).thenReturn(game);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(game))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(game.getUuid())))
                .andExpect(jsonPath("$.href").value(game.getHref()))
                .andExpect(jsonPath("$.name").value(game.getName()))
                .andExpect(jsonPath("$.description").value(game.getDescription()))
                .andExpect(jsonPath("$.trailer").value(game.getTrailer()))
                .andExpect(jsonPath("$.language").value(game.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.release_date").value(String.valueOf(game.getReleaseDate())))
                .andExpect(jsonPath("$.website").value(game.getWebsite()))
                .andExpect(jsonPath("$.playtime").value(String.valueOf(game.getPlaytime())))
                .andExpect(jsonPath("$.age_rating").value(game.getAgeRating()))
                .andExpect(jsonPath("$.stores").isNotEmpty())
                .andExpect(jsonPath("$.modes").isNotEmpty())
                .andExpect(jsonPath("$.publishers").isNotEmpty())
                .andExpect(jsonPath("$.developers").isNotEmpty())
                .andExpect(jsonPath("$.platforms").isNotEmpty())
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(game.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).save(any(GameDTO.class));
    }

    @Test
    void shouldSaveGameTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.saveTranslation(any(UUID.class), any(GameTranslationDTO.class))).thenReturn(translation);

        mockMvc.perform(post(URL + "/" + game.getUuid() + "/translations")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.name").value(translation.getName()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()))
                .andExpect(jsonPath("$.trailer").value(translation.getTrailer()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).saveTranslation(any(UUID.class), any(GameTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveGame() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(game))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveGameTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + game.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveGame() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(game))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveGameTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + game.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveGame() throws Exception {
        game.setReleaseDate(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(game))
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
        translation.setName(null);

        String url = URL + "/" + game.getUuid() + "/translations";
        mockMvc.perform(post(url)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
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
        patch.setReleaseDate(LocalDate.parse("2000-01-01"));

        doNothing().when(service).patch(any(UUID.class), any(GameDTO.class));

        mockMvc.perform(patch(URL + "/" + game.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(patch.getReleaseDate())));

        verify(service, times(1)).patch(any(UUID.class), any(GameDTO.class));
    }

    @Test
    void shouldUpdateGameTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");
        GameTranslationDTO patch = new GameTranslationDTO();
        patch.setName("Lorem");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        doNothing().when(service).patchTranslation(any(UUID.class), anyString(), any(GameTranslationDTO.class));

        mockMvc.perform(patch(URL + "/" + game.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.name").value(String.valueOf(patch.getName())));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1))
                .patchTranslation(any(UUID.class), anyString(), any(GameTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateGame() throws Exception {
        mockMvc.perform(patch(URL + "/" + game.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateGameTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + game.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateGame() throws Exception {
        mockMvc.perform(patch(URL + "/" + game.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateGameTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + game.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateGame() throws Exception {
        mockMvc.perform(patch(URL + "/" + game.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateGameTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + game.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGame() throws Exception {
        GameDTO patch = new GameDTO();
        patch.setReleaseDate(LocalDate.parse("2000-01-01"));

        doThrow(ItemNotFoundException.class).when(service).patch(any(UUID.class), any(GameDTO.class));

        mockMvc.perform(patch(URL + "/" + game.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patch(any(UUID.class), any(GameDTO.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGameTranslation() throws Exception {
        GameTranslationDTO patch = new GameTranslationDTO();
        patch.setName("Ipsum");

        doThrow(ItemNotFoundException.class).when(service).patchTranslation(any(UUID.class),
                anyString(),
                any(GameTranslationDTO.class)
        );

        mockMvc.perform(patch(URL + "/" + game.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patchTranslation(any(UUID.class),
                anyString(),
                any(GameTranslationDTO.class)
        );
    }

    @Test
    void shouldDeleteGame() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + game.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldDeleteGameTranslation() throws Exception {
        doNothing().when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + game.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteGame() throws Exception {
        mockMvc.perform(delete(URL + "/" + game.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteGameTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + game.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteGame() throws Exception {
        mockMvc.perform(delete(URL + "/" + game.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteGameTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + game.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGameOnDeleteGame() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + game.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGameOnDeleteGameTranslation() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + game.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }
}