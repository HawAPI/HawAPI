package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.SeasonDTO;
import com.lucasjosino.hawapi.models.dto.translation.SeasonTranslationDTO;
import com.lucasjosino.hawapi.services.impl.SeasonServiceImpl;
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
@WebMvcTest(controllers = SeasonController.class)
@ContextConfiguration(classes = {SeasonController.class, ControllerAdvisor.class, SecurityConfig.class})
class SeasonControllerTest {

    private static final String URL = "/api/v1/seasons";

    private SeasonDTO season;

    private SeasonTranslationDTO translation;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeasonServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        season = new SeasonDTO();
        season.setUuid(UUID.randomUUID());
        season.setHref(URL + "/" + season.getUuid());
        season.setLanguages(Collections.singletonList("Lorem"));
        season.setDurationTotal(215398753);
        season.setSeasonNum((byte) 2);
        season.setReleaseDate(LocalDate.now());
        season.setNextSeason("/api/v1/seasons/3");
        season.setPrevSeason("/api/v1/seasons/1");
        season.setEpisodes(Arrays.asList("/api/v1/episodes/1", "/api/v1/episodes/2", "/api/v1/episodes/3"));
        season.setSoundtracks(Arrays.asList("/api/v1/soundtracks/1", "/api/v1/soundtracks/2", "/api/v1/soundtracks/3"));
        season.setBudget(218459);
        season.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        season.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        season.setSources(Arrays.asList("https://example.com", "https://example.com"));
        season.setCreatedAt(LocalDateTime.now());
        season.setUpdatedAt(LocalDateTime.now());
        season.setLanguage("en-US");
        season.setTitle("Lorem Ipsum");
        season.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        season.setGenres(Arrays.asList("gen1", "gen2", "gen3"));
        season.setTrailers(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));

        translation = new SeasonTranslationDTO();
        translation.setLanguage("en-US");
        translation.setTitle("Lorem Ipsum");
        translation.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translation.setGenres(Arrays.asList("gen1", "gen2", "gen3"));
        translation.setTrailers(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));
    }

    @Test
    void shouldReturnAllSeasons() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, "en-US");

        when(service.findAllUUIDs(any(Pageable.class), anyLong())).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class), anyLong()))
                .thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(season));

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

        verify(service, times(1)).findAllUUIDs(any(Pageable.class), anyLong());
        verify(responseUtils, times(1)).getHeaders(
                any(),
                any(Pageable.class),
                nullable(String.class),
                anyLong()
        );
        verify(service, times(1)).findAll(anyMap(), anyList());
    }

    @Test
    void shouldReturnAllSeasonsWithPortugueseLanguage() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, "pt-BR");

        when(service.findAllUUIDs(any(Pageable.class), anyLong())).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class), anyLong()))
                .thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(season));

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

        verify(service, times(1)).findAllUUIDs(any(Pageable.class), anyLong());
        verify(responseUtils, times(1)).getHeaders(
                any(),
                any(Pageable.class),
                nullable(String.class),
                anyLong()
        );
        verify(service, times(1)).findAll(anyMap(), anyList());
    }

    @Test
    void shouldReturnAllSeasonTranslations() throws Exception {
        when(service.findAllTranslationsBy(any(UUID.class))).thenReturn(Collections.singletonList(translation));

        mockMvc.perform(get(URL + "/" + season.getUuid() + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(translation.getTitle()))
                .andExpect(jsonPath("$[0].description").value(translation.getDescription()))
                .andExpect(jsonPath("$[0].language").value(translation.getLanguage()))
                .andExpect(jsonPath("$[0].genres").isNotEmpty())
                .andExpect(jsonPath("$[0].trailers").isNotEmpty());

        verify(service, times(1)).findAllTranslationsBy(any(UUID.class));
    }

    @Test
    void whenNoSeasonLanguageFoundShouldThrowItemNotFoundExceptionOnAllSeasonsWithPortugueseLanguage() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + season.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllSeasons() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.emptyList();
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 0
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, null);

        when(service.findAllUUIDs(any(Pageable.class), anyLong())).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class), anyLong()))
                .thenReturn(headers);
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

        verify(service, times(1)).findAllUUIDs(any(Pageable.class), anyLong());
        verify(responseUtils, times(1)).getHeaders(
                any(),
                any(Pageable.class),
                nullable(String.class),
                anyLong()
        );
        verify(service, times(1)).findAll(anyMap(), anyList());
    }

    @Test
    void shouldReturnRandomSeason() throws Exception {
        when(service.findRandom(nullable(String.class))).thenReturn(season);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(season.getUuid())))
                .andExpect(jsonPath("$.href").value(season.getHref()))
                .andExpect(jsonPath("$.title").value(season.getTitle()))
                .andExpect(jsonPath("$.description").value(season.getDescription()))
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.trailers").isNotEmpty())
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.duration_total").value(String.valueOf(season.getDurationTotal())))
                .andExpect(jsonPath("$.season_num").value(String.valueOf(season.getSeasonNum())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(season.getReleaseDate())))
                .andExpect(jsonPath("$.next_season").value(season.getNextSeason()))
                .andExpect(jsonPath("$.prev_season").value(season.getPrevSeason()))
                .andExpect(jsonPath("$.episodes").isNotEmpty())
                .andExpect(jsonPath("$.soundtracks").isNotEmpty())
                .andExpect(jsonPath("$.budget").value(String.valueOf(season.getBudget())))
                .andExpect(jsonPath("$.thumbnail").value(season.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnRandomSeasonTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("en-US");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findRandomTranslation(any(UUID.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + season.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.title").value(translation.getTitle()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()))
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.trailers").isNotEmpty());

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomSeasonTranslation() throws Exception {
        when(service.findRandomTranslation(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + season.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnRandomSeason() throws Exception {
        when(service.findRandom(nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnSeasonByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenReturn(season);

        mockMvc.perform(get(URL + "/" + season.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(season.getUuid())))
                .andExpect(jsonPath("$.href").value(season.getHref()))
                .andExpect(jsonPath("$.title").value(season.getTitle()))
                .andExpect(jsonPath("$.description").value(season.getDescription()))
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.trailers").isNotEmpty())
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.duration_total").value(String.valueOf(season.getDurationTotal())))
                .andExpect(jsonPath("$.season_num").value(String.valueOf(season.getSeasonNum())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(season.getReleaseDate())))
                .andExpect(jsonPath("$.next_season").value(season.getNextSeason()))
                .andExpect(jsonPath("$.prev_season").value(season.getPrevSeason()))
                .andExpect(jsonPath("$.episodes").isNotEmpty())
                .andExpect(jsonPath("$.soundtracks").isNotEmpty())
                .andExpect(jsonPath("$.budget").value(String.valueOf(season.getBudget())))
                .andExpect(jsonPath("$.thumbnail").value(season.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldReturnSeasonTranslationByUUIDAndLanguage() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + season.getUuid() + "/translations/pt-BR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.title").value(translation.getTitle()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()))
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.trailers").isNotEmpty());

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnSeasonByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + season.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoSeasonTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + season.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldSaveSeason() throws Exception {
        when(service.save(any(SeasonDTO.class))).thenReturn(season);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(season))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(season.getUuid())))
                .andExpect(jsonPath("$.href").value(season.getHref()))
                .andExpect(jsonPath("$.title").value(season.getTitle()))
                .andExpect(jsonPath("$.description").value(season.getDescription()))
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.trailers").isNotEmpty())
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.duration_total").value(String.valueOf(season.getDurationTotal())))
                .andExpect(jsonPath("$.season_num").value(String.valueOf(season.getSeasonNum())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(season.getReleaseDate())))
                .andExpect(jsonPath("$.next_season").value(season.getNextSeason()))
                .andExpect(jsonPath("$.prev_season").value(season.getPrevSeason()))
                .andExpect(jsonPath("$.episodes").isNotEmpty())
                .andExpect(jsonPath("$.soundtracks").isNotEmpty())
                .andExpect(jsonPath("$.budget").value(String.valueOf(season.getBudget())))
                .andExpect(jsonPath("$.thumbnail").value(season.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).save(any(SeasonDTO.class));
    }

    @Test
    void shouldSaveSeasonTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.saveTranslation(any(UUID.class), any(SeasonTranslationDTO.class))).thenReturn(translation);

        mockMvc.perform(post(URL + "/" + season.getUuid() + "/translations")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.title").value(translation.getTitle()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()))
                .andExpect(jsonPath("$.genres").isNotEmpty())
                .andExpect(jsonPath("$.trailers").isNotEmpty());

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).saveTranslation(any(UUID.class), any(SeasonTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveSeason() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(season))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveSeasonTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + season.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveSeason() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(season))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveSeasonTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + season.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveSeason() throws Exception {
        season.setThumbnail("http://cdn.theproject.id/hawapi/image.jpg");
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(season))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'thumbnail' doesn't have a valid image URL"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveSeasonTranslation() throws Exception {
        translation.setTitle(null);

        String url = URL + "/" + season.getUuid() + "/translations";
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
                .andExpect(jsonPath("$.message").value("Field 'title' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(url));
    }


    @Test
    void shouldUpdateSeason() throws Exception {
        SeasonDTO patch = new SeasonDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");

        doNothing().when(service).patch(any(UUID.class), any(SeasonDTO.class));

        mockMvc.perform(patch(URL + "/" + season.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.thumbnail").value(String.valueOf(patch.getThumbnail())));

        verify(service, times(1)).patch(any(UUID.class), any(SeasonDTO.class));
    }

    @Test
    void shouldUpdateSeasonTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");
        SeasonTranslationDTO patch = new SeasonTranslationDTO();
        patch.setTitle("Lorem");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        doNothing().when(service).patchTranslation(any(UUID.class), anyString(), any(SeasonTranslationDTO.class));

        mockMvc.perform(patch(URL + "/" + season.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.title").value(String.valueOf(patch.getTitle())));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1))
                .patchTranslation(any(UUID.class), anyString(), any(SeasonTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateSeason() throws Exception {
        mockMvc.perform(patch(URL + "/" + season.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateSeasonTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + season.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateSeason() throws Exception {
        mockMvc.perform(patch(URL + "/" + season.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateSeasonTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + season.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateSeason() throws Exception {
        mockMvc.perform(patch(URL + "/" + season.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateSeasonTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + season.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeason() throws Exception {
        SeasonDTO patch = new SeasonDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");

        doThrow(ItemNotFoundException.class).when(service).patch(any(UUID.class), any(SeasonDTO.class));

        mockMvc.perform(patch(URL + "/" + season.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patch(any(UUID.class), any(SeasonDTO.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeasonTranslation() throws Exception {
        SeasonTranslationDTO patch = new SeasonTranslationDTO();
        patch.setTitle("Ipsum");

        doThrow(ItemNotFoundException.class).when(service).patchTranslation(any(UUID.class),
                anyString(),
                any(SeasonTranslationDTO.class)
        );

        mockMvc.perform(patch(URL + "/" + season.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patchTranslation(any(UUID.class),
                anyString(),
                any(SeasonTranslationDTO.class)
        );
    }

    @Test
    void shouldDeleteSeason() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + season.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldDeleteSeasonTranslation() throws Exception {
        doNothing().when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + season.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteSeason() throws Exception {
        mockMvc.perform(delete(URL + "/" + season.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteSeasonTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + season.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteSeason() throws Exception {
        mockMvc.perform(delete(URL + "/" + season.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteSeasonTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + season.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeasonOnDeleteSeason() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + season.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeasonOnDeleteSeasonTranslation() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + season.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }
}