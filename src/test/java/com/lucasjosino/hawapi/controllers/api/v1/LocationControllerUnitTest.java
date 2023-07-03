package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.LocationDTO;
import com.lucasjosino.hawapi.models.dto.translation.LocationTranslationDTO;
import com.lucasjosino.hawapi.services.impl.LocationServiceImpl;
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
@WebMvcTest(controllers = LocationController.class)
@ContextConfiguration(classes = {LocationController.class, ControllerAdvisor.class, SecurityConfig.class})
class LocationControllerUnitTest {

    private static final String URL = "/api/v1/locations";

    private LocationDTO location;

    private LocationTranslationDTO translation;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        location = new LocationDTO();
        location.setUuid(UUID.randomUUID());
        location.setHref(URL + "/" + location.getUuid());
        location.setLanguages(Collections.singletonList("Lorem"));
        location.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        location.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        location.setSources(Arrays.asList("https://example.com", "https://example.com"));
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        location.setLanguage("en-US");
        location.setName("Lorem Ipsum");
        location.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translation = new LocationTranslationDTO();
        translation.setLanguage("en-US");
        translation.setName("Lorem Ipsum");
        translation.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
    }

    @Test
    void shouldReturnAllLocations() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, null);

        when(service.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class))).thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(location));

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
    void shouldReturnAllLocationsWithPortugueseLanguage() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, "pt-BR");

        when(service.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class))).thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(location));

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
    void shouldReturnAllLocationTranslations() throws Exception {
        when(service.findAllTranslationsBy(any(UUID.class))).thenReturn(Collections.singletonList(translation));

        mockMvc.perform(get(URL + "/" + location.getUuid() + "/translations"))
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
    void whenNoLocationLanguageFoundShouldThrowItemNotFoundExceptionOnAllLocationsWithPortugueseLanguage() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + location.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllLocations() throws Exception {
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
    void shouldReturnRandomLocation() throws Exception {
        when(service.findRandom(nullable(String.class))).thenReturn(location);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(location.getUuid())))
                .andExpect(jsonPath("$.href").value(location.getHref()))
                .andExpect(jsonPath("$.name").value(location.getName()))
                .andExpect(jsonPath("$.description").value(location.getDescription()))
                .andExpect(jsonPath("$.language").value(location.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(location.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnRandomLocationTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("en-US");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findRandomTranslation(any(UUID.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + location.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.name").value(translation.getName()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomLocationTranslation() throws Exception {
        when(service.findRandomTranslation(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + location.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandomTranslation(any(UUID.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnRandomLocation() throws Exception {
        when(service.findRandom(nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnLocationByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenReturn(location);

        mockMvc.perform(get(URL + "/" + location.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(location.getUuid())))
                .andExpect(jsonPath("$.href").value(location.getHref()))
                .andExpect(jsonPath("$.name").value(location.getName()))
                .andExpect(jsonPath("$.description").value(location.getDescription()))
                .andExpect(jsonPath("$.language").value(location.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(location.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldReturnLocationTranslationByUUIDAndLanguage() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenReturn(translation);

        mockMvc.perform(get(URL + "/" + location.getUuid() + "/translations/pt-BR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "pt-BR"))
                .andExpect(jsonPath("$.name").value(translation.getName()))
                .andExpect(jsonPath("$.description").value(translation.getDescription()))
                .andExpect(jsonPath("$.language").value(translation.getLanguage()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnLocationByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + location.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoLocationTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        when(service.findTranslationBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + location.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findTranslationBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldSaveLocation() throws Exception {
        when(service.save(any(LocationDTO.class))).thenReturn(location);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(location))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(location.getUuid())))
                .andExpect(jsonPath("$.href").value(location.getHref()))
                .andExpect(jsonPath("$.name").value(location.getName()))
                .andExpect(jsonPath("$.description").value(location.getDescription()))
                .andExpect(jsonPath("$.language").value(location.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(location.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).save(any(LocationDTO.class));
    }

    @Test
    void shouldSaveLocationTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        when(service.saveTranslation(any(UUID.class), any(LocationTranslationDTO.class))).thenReturn(translation);

        mockMvc.perform(post(URL + "/" + location.getUuid() + "/translations")
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
                .andExpect(jsonPath("$.language").value(translation.getLanguage()));

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1)).saveTranslation(any(UUID.class), any(LocationTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveLocation() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(location))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveLocationTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + location.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveLocation() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(location))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveLocationTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + location.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveLocation() throws Exception {
        location.setThumbnail("http://cdn.theproject.id/hawapi/image.jpg");
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(location))
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
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveLocationTranslation() throws Exception {
        translation.setName(null);

        String url = URL + "/" + location.getUuid() + "/translations";
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
    void shouldUpdateLocation() throws Exception {
        LocationDTO patch = new LocationDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");

        doNothing().when(service).patch(any(UUID.class), any(LocationDTO.class));

        mockMvc.perform(patch(URL + "/" + location.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.thumbnail").value(String.valueOf(patch.getThumbnail())));

        verify(service, times(1)).patch(any(UUID.class), any(LocationDTO.class));
    }

    @Test
    void shouldUpdateLocationTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("pt-BR");
        LocationTranslationDTO patch = new LocationTranslationDTO();
        patch.setName("Lorem");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        doNothing().when(service).patchTranslation(any(UUID.class), anyString(), any(LocationTranslationDTO.class));

        mockMvc.perform(patch(URL + "/" + location.getUuid() + "/translations/en-US")
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
                .patchTranslation(any(UUID.class), anyString(), any(LocationTranslationDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateLocation() throws Exception {
        mockMvc.perform(patch(URL + "/" + location.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateLocationTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + location.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateLocation() throws Exception {
        mockMvc.perform(patch(URL + "/" + location.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateLocationTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + location.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateLocation() throws Exception {
        mockMvc.perform(patch(URL + "/" + location.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateLocationTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + location.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocation() throws Exception {
        LocationDTO patch = new LocationDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");

        doThrow(ItemNotFoundException.class).when(service).patch(any(UUID.class), any(LocationDTO.class));

        mockMvc.perform(patch(URL + "/" + location.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patch(any(UUID.class), any(LocationDTO.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocationTranslation() throws Exception {
        LocationTranslationDTO patch = new LocationTranslationDTO();
        patch.setName("Ipsum");

        doThrow(ItemNotFoundException.class).when(service).patchTranslation(any(UUID.class),
                anyString(),
                any(LocationTranslationDTO.class)
        );

        mockMvc.perform(patch(URL + "/" + location.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patchTranslation(any(UUID.class),
                anyString(),
                any(LocationTranslationDTO.class)
        );
    }

    @Test
    void shouldDeleteLocation() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + location.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldDeleteLocationTranslation() throws Exception {
        doNothing().when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + location.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteLocation() throws Exception {
        mockMvc.perform(delete(URL + "/" + location.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteLocationTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + location.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteLocation() throws Exception {
        mockMvc.perform(delete(URL + "/" + location.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteLocationTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + location.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocationOnDeleteLocation() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + location.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocationOnDeleteLocationTranslation() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteTranslation(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + location.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteTranslation(any(UUID.class), anyString());
    }
}