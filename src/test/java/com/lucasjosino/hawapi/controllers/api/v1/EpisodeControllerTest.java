package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import com.lucasjosino.hawapi.models.dto.translation.EpisodeTranslationDTO;
import com.lucasjosino.hawapi.services.impl.EpisodeServiceImpl;
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
@WebMvcTest(controllers = EpisodeController.class)
@ContextConfiguration(classes = {EpisodeController.class, ControllerAdvisor.class, SecurityConfig.class})
class EpisodeControllerTest {

    private static final String URL = "/api/v1/episodes";

    private EpisodeDTO episode;

    private EpisodeTranslationDTO translation;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EpisodeServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        episode = new EpisodeDTO();
        episode.setUuid(UUID.randomUUID());
        episode.setHref(URL + "/" + episode.getUuid());
        episode.setDuration(12482342);
        episode.setEpisodeNum((byte) 2);
        episode.setNextEpisode("/api/v1/episodes/3");
        episode.setPrevEpisode("/api/v1/episodes/1");
        episode.setSeason("/api/v1/seasons/1");
        episode.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        episode.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        episode.setSources(Arrays.asList("https://example.com", "https://example.com"));
        episode.setCreatedAt(LocalDateTime.now());
        episode.setUpdatedAt(LocalDateTime.now());
        episode.setLanguage("en-US");
        episode.setTitle("Lorem Ipsum");
        episode.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translation = new EpisodeTranslationDTO();
        translation.setLanguage("en-US");
        translation.setTitle("Lorem Ipsum");
        translation.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
    }

    @Test
    void shouldReturnAllEpisodes() throws Exception {
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
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(episode));

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
    void shouldReturnAllEpisodesWithPortugueseLanguage() throws Exception {
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
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(episode));

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
    void shouldReturnAllEpisodeTranslations() throws Exception {
        when(service.findAllTranslationsBy(any(UUID.class))).thenReturn(Collections.singletonList(translation));

        mockMvc.perform(get(URL + "/" + episode.getUuid() + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(translation.getTitle()))
                .andExpect(jsonPath("$[0].description").value(translation.getDescription()))
                .andExpect(jsonPath("$[0].language").value(translation.getLanguage()));

        verify(service, times(1)).findAllTranslationsBy(any(UUID.class));
    }

    @Test
    void whenNoEpisodeLanguageFoundShouldThrowItemNotFoundExceptionOnAllEpisodesWithPortugueseLanguage() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + episode.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllEpisodes() throws Exception {
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
    void shouldReturnRandomEpisode() throws Exception {
        when(service.findRandom(nullable(String.class))).thenReturn(episode);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(episode.getUuid())))
                .andExpect(jsonPath("$.href").value(episode.getHref()))
                .andExpect(jsonPath("$.title").value(episode.getTitle()))
                .andExpect(jsonPath("$.description").value(episode.getDescription()))
                .andExpect(jsonPath("$.language").value(episode.getLanguage()))
                .andExpect(jsonPath("$.duration").value(String.valueOf(episode.getDuration())))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episode.getEpisodeNum())))
                .andExpect(jsonPath("$.next_episode").value(episode.getNextEpisode()))
                .andExpect(jsonPath("$.prev_episode").value(episode.getPrevEpisode()))
                .andExpect(jsonPath("$.season").value(episode.getSeason()))
                .andExpect(jsonPath("$.thumbnail").value(episode.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnRandomEpisodeTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("en-US");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findRandomTranslation(any(UUID.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + episode.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.title").value(translation.getTitle()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomEpisodeTranslation() throws Exception {
        when(service.findRandomTranslation(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + episode.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnRandomEpisode() throws Exception {
        when(service.findRandom(nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnEpisodeByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenReturn(episode);

        mockMvc.perform(get(URL + "/" + episode.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(episode.getUuid())))
                .andExpect(jsonPath("$.href").value(episode.getHref()))
                .andExpect(jsonPath("$.title").value(episode.getTitle()))
                .andExpect(jsonPath("$.description").value(episode.getDescription()))
                .andExpect(jsonPath("$.language").value(episode.getLanguage()))
                .andExpect(jsonPath("$.duration").value(String.valueOf(episode.getDuration())))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episode.getEpisodeNum())))
                .andExpect(jsonPath("$.next_episode").value(episode.getNextEpisode()))
                .andExpect(jsonPath("$.prev_episode").value(episode.getPrevEpisode()))
                .andExpect(jsonPath("$.season").value(episode.getSeason()))
                .andExpect(jsonPath("$.thumbnail").value(episode.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldReturnEpisodeTranslationByUUIDAndLanguage() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + episode.getUuid() + "/translations/pt-BR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.title").value(translation.getTitle()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnEpisodeByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + episode.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoEpisodeTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + episode.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldSaveEpisode() throws Exception {
        when(service.save(any(EpisodeDTO.class))).thenReturn(episode);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episode))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(episode.getUuid())))
                .andExpect(jsonPath("$.href").value(episode.getHref()))
                .andExpect(jsonPath("$.title").value(episode.getTitle()))
                .andExpect(jsonPath("$.description").value(episode.getDescription()))
                .andExpect(jsonPath("$.language").value(episode.getLanguage()))
                .andExpect(jsonPath("$.duration").value(String.valueOf(episode.getDuration())))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(episode.getEpisodeNum())))
                .andExpect(jsonPath("$.next_episode").value(episode.getNextEpisode()))
                .andExpect(jsonPath("$.prev_episode").value(episode.getPrevEpisode()))
                .andExpect(jsonPath("$.season").value(episode.getSeason()))
                .andExpect(jsonPath("$.thumbnail").value(episode.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).save(any(EpisodeDTO.class));
    }

    @Test
    void shouldSaveEpisodeTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.saveTranslation(any(UUID.class), any(EpisodeTranslationDTO.class))).thenReturn(translation);

        mockMvc.perform(post(URL + "/" + episode.getUuid() + "/translations")
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
                .andExpect(jsonPath("$.language").value(translation.getLanguage()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).saveTranslation(any(UUID.class), any(EpisodeTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveEpisode() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episode))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveEpisodeTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + episode.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveEpisode() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episode))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveEpisodeTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + episode.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveEpisode() throws Exception {
        episode.setEpisodeNum(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(episode))
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
        translation.setTitle(null);

        String url = URL + "/" + episode.getUuid() + "/translations";
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
    void shouldUpdateEpisode() throws Exception {
        EpisodeDTO patch = new EpisodeDTO();
        patch.setEpisodeNum((byte) 5);

        doNothing().when(service).patch(any(UUID.class), any(EpisodeDTO.class));

        mockMvc.perform(patch(URL + "/" + episode.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.episode_num").value(String.valueOf(patch.getEpisodeNum())));

        verify(service, times(1)).patch(any(UUID.class), any(EpisodeDTO.class));
    }

    @Test
    void shouldUpdateEpisodeTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");
        EpisodeTranslationDTO patch = new EpisodeTranslationDTO();
        patch.setTitle("Lorem");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        doNothing().when(service).patchTranslation(any(UUID.class), anyString(), any(EpisodeTranslationDTO.class));

        mockMvc.perform(patch(URL + "/" + episode.getUuid() + "/translations/en-US")
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
                .patchTranslation(any(UUID.class), anyString(), any(EpisodeTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateEpisode() throws Exception {
        mockMvc.perform(patch(URL + "/" + episode.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateEpisodeTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + episode.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateEpisode() throws Exception {
        mockMvc.perform(patch(URL + "/" + episode.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateEpisodeTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + episode.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateEpisode() throws Exception {
        mockMvc.perform(patch(URL + "/" + episode.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateEpisodeTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + episode.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisode() throws Exception {
        EpisodeDTO patch = new EpisodeDTO();
        patch.setEpisodeNum((byte) 5);

        doThrow(ItemNotFoundException.class).when(service).patch(any(UUID.class), any(EpisodeDTO.class));

        mockMvc.perform(patch(URL + "/" + episode.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patch(any(UUID.class), any(EpisodeDTO.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisodeTranslation() throws Exception {
        EpisodeTranslationDTO patch = new EpisodeTranslationDTO();
        patch.setTitle("Ipsum");

        doThrow(ItemNotFoundException.class).when(service).patchTranslation(any(UUID.class),
                anyString(),
                any(EpisodeTranslationDTO.class)
        );

        mockMvc.perform(patch(URL + "/" + episode.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patchTranslation(any(UUID.class),
                anyString(),
                any(EpisodeTranslationDTO.class)
        );
    }

    @Test
    void shouldDeleteEpisode() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + episode.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldDeleteEpisodeTranslation() throws Exception {
        doNothing().when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + episode.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteEpisode() throws Exception {
        mockMvc.perform(delete(URL + "/" + episode.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteEpisodeTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + episode.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteEpisode() throws Exception {
        mockMvc.perform(delete(URL + "/" + episode.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteEpisodeTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + episode.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisodeOnDeleteEpisode() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + episode.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisodeOnDeleteEpisodeTranslation() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + episode.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }
}