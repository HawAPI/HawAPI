package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.models.dto.LocationDTO;
import com.lucasjosino.hawapi.models.dto.translation.LocationTranslationDTO;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import com.lucasjosino.hawapi.repositories.translation.LocationTranslationRepository;
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
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTestConfig
class LocationIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/locations/";

    private static final ModelMapper mapper = new ModelMapper();

    private LocationDTO locationDTO;

    private LocationTranslationDTO translationDTO;

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationTranslationRepository translationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        locationDTO = new LocationDTO();
        locationDTO.setUuid(UUID.randomUUID());
        locationDTO.setHref(URL + "/" + locationDTO.getUuid());
        locationDTO.setLanguages(Collections.singletonList("Lorem"));
        locationDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        locationDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        locationDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        locationDTO.setCreatedAt(LocalDateTime.now());
        locationDTO.setUpdatedAt(LocalDateTime.now());
        locationDTO.setLanguage("en-US");
        locationDTO.setName("Lorem Ipsum");
        locationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translationDTO = new LocationTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setName("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        LocationModel locationModel = mapper.map(locationDTO, LocationModel.class);
        repository.save(locationModel);

        LocationTranslation locationTranslation = mapper.map(translationDTO, LocationTranslation.class);
        locationTranslation.setLocationUuid(locationModel.getUuid());
        translationRepository.save(locationTranslation);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllLocations() throws Exception {
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
    void shouldReturnAllLocationsWithPortugueseLanguage() throws Exception {
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
    void shouldReturnAllLocationTranslations() throws Exception {
        mockMvc.perform(get(URL + "/" + locationDTO.getUuid() + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(translationDTO.getName()))
                .andExpect(jsonPath("$[0].description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$[0].language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoLocationLanguageFoundShouldThrowItemNotFoundExceptionOnAllLocationsWithPortugueseLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + locationDTO.getUuid() + "?language=pt-BR"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnAllLocations() throws Exception {
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
    void shouldReturnRandomLocation() throws Exception {
        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(locationDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(locationDTO.getHref()))
                .andExpect(jsonPath("$.name").value(locationDTO.getName()))
                .andExpect(jsonPath("$.description").value(locationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(locationDTO.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(locationDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnRandomLocationTranslation() throws Exception {
        mockMvc.perform(get(URL + "/" + locationDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Content-Language"))
                .andExpect(jsonPath("$.name").value(translationDTO.getName()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoTranslationFoundShouldThrowItemNotFoundExceptionOnRandomLocationTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + locationDTO.getUuid() + "/translations/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnRandomLocation() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnLocationByUUID() throws Exception {
        mockMvc.perform(get(URL + "/" + locationDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(locationDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(locationDTO.getHref()))
                .andExpect(jsonPath("$.name").value(locationDTO.getName()))
                .andExpect(jsonPath("$.description").value(locationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(locationDTO.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(locationDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnLocationTranslationByUUIDAndLanguage() throws Exception {
        mockMvc.perform(get(URL + "/" + locationDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.name").value(translationDTO.getName()))
                .andExpect(jsonPath("$.description").value(translationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(translationDTO.getLanguage()));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnLocationByUUID() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/" + locationDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoLocationTranslationFoundShouldThrowItemNotFoundExceptionOnTranslationByUUIDAndLanguage() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(get(URL + "/" + locationDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveLocation() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(locationDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(locationDTO.getUuid())))
                .andExpect(jsonPath("$.href").value(locationDTO.getHref()))
                .andExpect(jsonPath("$.name").value(locationDTO.getName()))
                .andExpect(jsonPath("$.description").value(locationDTO.getDescription()))
                .andExpect(jsonPath("$.language").value(locationDTO.getLanguage()))
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.thumbnail").value(locationDTO.getThumbnail()))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldSaveLocationTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(post(URL + "/" + locationDTO.getUuid() + "/translations")
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
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveLocation() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(locationDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveLocationTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + locationDTO.getUuid() + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveLocation() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(locationDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveLocationTranslation() throws Exception {
        mockMvc.perform(post(URL + "/" + locationDTO.getUuid() + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translationDTO))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveLocation() throws Exception {
        locationDTO.setName(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(locationDTO))
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
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveLocationTranslation() throws Exception {
        translationDTO.setName(null);

        String url = URL + locationDTO.getUuid() + "/translations";
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
    void shouldUpdateLocation() throws Exception {
        LocationDTO patch = new LocationDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image2.jpg");

        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid())
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
    void shouldUpdateLocationTranslation() throws Exception {
        LocationTranslationDTO patch = new LocationTranslationDTO();
        patch.setName("Lorem");

        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
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
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateLocation() throws Exception {
        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateLocationTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateLocation() throws Exception {
        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateLocationTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateLocation() throws Exception {
        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateLocationTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocation() throws Exception {
        repository.deleteAll();

        LocationDTO patch = new LocationDTO();
        patch.setThumbnail("https://cdn.theproject.id/hawapi/image2.jpg");

        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocationTranslation() throws Exception {
        translationRepository.deleteAll();

        LocationTranslationDTO patch = new LocationTranslationDTO();
        patch.setName("Ipsum");

        mockMvc.perform(patch(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteLocation() throws Exception {
        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteLocationTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteLocation() throws Exception {
        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteLocationTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid() + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteLocation() throws Exception {
        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteLocationTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocationOnDeleteLocation() throws Exception {
        repository.deleteAll();

        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocationOnDeleteLocationTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(delete(URL + "/" + locationDTO.getUuid() + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}