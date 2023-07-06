package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.services.impl.SoundtrackServiceImpl;
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
@WebMvcTest(controllers = SoundtrackController.class)
@ContextConfiguration(classes = {SoundtrackController.class, ControllerAdvisor.class, SecurityConfig.class})
class SoundtrackControllerTest {

    private static final String URL = "/api/v1/soundtracks";

    private SoundtrackDTO soundtrack;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SoundtrackServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        soundtrack = new SoundtrackDTO();
        soundtrack.setUuid(UUID.randomUUID());
        soundtrack.setHref(URL + "/" + soundtrack.getUuid());
        soundtrack.setName("Lorem");
        soundtrack.setArtist("Ipsum");
        soundtrack.setAlbum("Lorem Ipsum");
        soundtrack.setUrls(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));
        soundtrack.setDuration(158351809);
        soundtrack.setReleaseDate(LocalDate.now());
        soundtrack.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        soundtrack.setSources(Arrays.asList("https://example.com", "https://example.com"));
        soundtrack.setCreatedAt(LocalDateTime.now());
        soundtrack.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldReturnAllSoundtracks() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, null);

        when(service.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class))).thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(soundtrack));

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
    void whenNoUUIDIsFoundShouldReturnEmptyList() throws Exception {
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
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).findAllUUIDs(any(Pageable.class));
        verify(responseUtils, times(1)).getHeaders(any(), any(Pageable.class), nullable(String.class));
        verify(service, times(1)).findAll(anyMap(), anyList());
    }


    @Test
    void shouldReturnRandomSoundtrack() throws Exception {
        when(service.findRandom(nullable(String.class))).thenReturn(soundtrack);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(soundtrack.getUuid())))
                .andExpect(jsonPath("$.href").value(soundtrack.getHref()))
                .andExpect(jsonPath("$.name").value(soundtrack.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrack.getArtist()))
                .andExpect(jsonPath("$.album").value(soundtrack.getAlbum()))
                .andExpect(jsonPath("$.urls").isNotEmpty())
                .andExpect(jsonPath("$.duration").value(String.valueOf(soundtrack.getDuration())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrack.getReleaseDate())))
                .andExpect(jsonPath("$.thumbnail").value(soundtrack.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnRandomSoundtrack() throws Exception {
        when(service.findRandom(nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnSoundtrackByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenReturn(soundtrack);

        mockMvc.perform(get(URL + "/" + soundtrack.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(soundtrack.getUuid())))
                .andExpect(jsonPath("$.href").value(soundtrack.getHref()))
                .andExpect(jsonPath("$.name").value(soundtrack.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrack.getArtist()))
                .andExpect(jsonPath("$.album").value(soundtrack.getAlbum()))
                .andExpect(jsonPath("$.urls").isNotEmpty())
                .andExpect(jsonPath("$.duration").value(String.valueOf(soundtrack.getDuration())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrack.getReleaseDate())))
                .andExpect(jsonPath("$.thumbnail").value(soundtrack.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnSoundtrackByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + soundtrack.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldSaveSoundtrack() throws Exception {
        when(service.save(any(SoundtrackDTO.class))).thenReturn(soundtrack);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrack))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(soundtrack.getUuid())))
                .andExpect(jsonPath("$.href").value(soundtrack.getHref()))
                .andExpect(jsonPath("$.name").value(soundtrack.getName()))
                .andExpect(jsonPath("$.artist").value(soundtrack.getArtist()))
                .andExpect(jsonPath("$.album").value(soundtrack.getAlbum()))
                .andExpect(jsonPath("$.urls").isNotEmpty())
                .andExpect(jsonPath("$.duration").value(String.valueOf(soundtrack.getDuration())))
                .andExpect(jsonPath("$.release_date").value(String.valueOf(soundtrack.getReleaseDate())))
                .andExpect(jsonPath("$.thumbnail").value(soundtrack.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).save(any(SoundtrackDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveSoundtrack() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrack))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveSoundtrack() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrack))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveSoundtrack() throws Exception {
        soundtrack.setName(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(soundtrack))
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

        doNothing().when(service).patch(any(UUID.class), any(SoundtrackDTO.class));

        mockMvc.perform(patch(URL + "/" + soundtrack.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.duration").value(String.valueOf(patch.getDuration())));

        verify(service, times(1)).patch(any(UUID.class), any(SoundtrackDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateSoundtrack() throws Exception {
        mockMvc.perform(patch(URL + "/" + soundtrack.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateSoundtrack() throws Exception {
        mockMvc.perform(patch(URL + "/" + soundtrack.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateSoundtrack() throws Exception {
        mockMvc.perform(patch(URL + "/" + soundtrack.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnUpdateSoundtrack() throws Exception {
        SoundtrackDTO patch = new SoundtrackDTO();
        patch.setDuration(1247149);

        doThrow(ItemNotFoundException.class).when(service).patch(any(UUID.class), any(SoundtrackDTO.class));

        mockMvc.perform(patch(URL + "/" + soundtrack.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patch(any(UUID.class), any(SoundtrackDTO.class));
    }

    @Test
    void shouldDeleteSoundtrack() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + soundtrack.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete(URL + "/" + soundtrack.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteSoundtrack() throws Exception {
        mockMvc.perform(delete(URL + "/" + soundtrack.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnUpdateSoundtrackOnDeleteSoundtrack() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + soundtrack.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(any(UUID.class));
    }
}