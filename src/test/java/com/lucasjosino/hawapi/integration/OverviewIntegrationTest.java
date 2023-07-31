package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.OverviewModel;
import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import com.lucasjosino.hawapi.models.dto.translation.OverviewTranslationDTO;
import com.lucasjosino.hawapi.models.translations.OverviewTranslation;
import com.lucasjosino.hawapi.repositories.OverviewRepository;
import com.lucasjosino.hawapi.repositories.translation.OverviewTranslationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
public class OverviewIntegrationTest extends DatabaseContainerInitializer {

    private static final String URL = "/api/v1/overview";

    private static final ModelMapper mapper = new ModelMapper();

    private OverviewDTO overview;

    private OverviewTranslationDTO translation;

    @Autowired
    private OverviewRepository repository;

    @Autowired
    private OverviewTranslationRepository translationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        overview = new OverviewDTO();
        overview.setUuid(UUID.randomUUID());
        overview.setHref("/api/v1/overview/" + overview.getUuid());
        overview.setCreators(Collections.singletonList("Lorem"));
        overview.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        overview.setSources(Arrays.asList("https://example.com", "https://example.com"));
        overview.setCreatedAt(LocalDateTime.now());
        overview.setUpdatedAt(LocalDateTime.now());
        overview.setLanguage("en-US");
        overview.setTitle("Lorem Ipsum");
        overview.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translation = new OverviewTranslationDTO();
        translation.setLanguage("en-US");
        translation.setTitle("Lorem Ipsum");
        translation.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        OverviewModel overviewModel = mapper.map(overview, OverviewModel.class);
        repository.save(overviewModel);

        OverviewTranslation overviewTranslation = mapper.map(translation, OverviewTranslation.class);
        overviewTranslation.setOverviewUuid(overviewModel.getUuid());
        translationRepository.save(overviewTranslation);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllOverviewTranslations() throws Exception {
        mockMvc.perform(get(URL + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenNoTranslationFoundShouldReturnEmptyListOnReturnAllOverviewTranslations() throws Exception {
        translationRepository.deleteAll();

        Cache cache = cacheManager.getCache("findAllTranslation");
        if (cache != null) cache.clear();

        mockMvc.perform(get(URL + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnOverview() throws Exception {
        mockMvc.perform(get(URL + "?language=en-US"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(overview.getUuid())))
                .andExpect(jsonPath("$.href").value(overview.getHref()))
                .andExpect(jsonPath("$.title").value(overview.getTitle()))
                .andExpect(jsonPath("$.description").value(overview.getDescription()))
                .andExpect(jsonPath("$.thumbnail").value(overview.getThumbnail()))
                .andExpect(jsonPath("$.language").value(overview.getLanguage()))
                .andExpect(jsonPath("$.creators").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void shouldReturnOverviewWithDefaultLanguage() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(overview.getUuid())))
                .andExpect(jsonPath("$.href").value(overview.getHref()))
                .andExpect(jsonPath("$.title").value(overview.getTitle()))
                .andExpect(jsonPath("$.description").value(overview.getDescription()))
                .andExpect(jsonPath("$.thumbnail").value(overview.getThumbnail()))
                .andExpect(jsonPath("$.language").value(overview.getLanguage()))
                .andExpect(jsonPath("$.creators").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoOverviewFoundShouldReturnItemNotFoundExceptionOnReturnOverview() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void shouldReturnOverviewTranslationBy() throws Exception {
        mockMvc.perform(get(URL + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(overview.getTitle()))
                .andExpect(jsonPath("$.description").value(overview.getDescription()))
                .andExpect(jsonPath("$.language").value(overview.getLanguage()));
    }

    @Test
    void whenNoOverviewFoundShouldReturnItemNotFoundExceptionOnReturnOverviewTranslationBy() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get(URL + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/translations/en-US"));
    }

    @Test
    void shouldSaveOverview() throws Exception {
        repository.deleteAll();

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(overview))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.title").value(overview.getTitle()))
                .andExpect(jsonPath("$.description").value(overview.getDescription()))
                .andExpect(jsonPath("$.thumbnail").value(overview.getThumbnail()))
                .andExpect(jsonPath("$.language").value(overview.getLanguage()))
                .andExpect(jsonPath("$.creators").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveOverview() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(overview))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveOverview() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(overview))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveOverview() throws Exception {
        overview.setTitle(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(overview))
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
    void shouldSaveOverviewTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(post(URL + "/translations")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(overview.getTitle()))
                .andExpect(jsonPath("$.description").value(overview.getDescription()))
                .andExpect(jsonPath("$.language").value(overview.getLanguage()));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveOverviewTranslation() throws Exception {
        mockMvc.perform(post(URL + "/translations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveOverviewTranslation() throws Exception {
        mockMvc.perform(post(URL + "/translations")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(translation))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveOverviewTranslation() throws Exception {
        translation.setTitle(null);

        String url = URL + "/translations";
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
    void shouldUpdateOverview() throws Exception {
        OverviewDTO patch = new OverviewDTO();
        patch.setThumbnail("https://example.com/image.jpg");

        mockMvc.perform(patch(URL)
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
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateOverview() throws Exception {
        mockMvc.perform(patch(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateOverview() throws Exception {
        mockMvc.perform(patch(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnUpdateOverview() throws Exception {
        mockMvc.perform(patch(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoOverviewFoundShouldThrowItemNotFoundExceptionOnUpdateOverview() throws Exception {
        repository.deleteAll();

        OverviewDTO patch = new OverviewDTO();
        patch.setThumbnail("https://example.com/image.jpg");

        mockMvc.perform(patch(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateOverviewTranslation() throws Exception {
        OverviewTranslationDTO patch = new OverviewTranslationDTO();
        patch.setTitle("Lorem");

        mockMvc.perform(patch(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(patch.getTitle()));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateOverviewTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/translations/en-US")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateOverviewTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateOverviewTranslation() throws Exception {
        mockMvc.perform(patch(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoOverviewFoundShouldThrowItemNotFoundExceptionOnUpdateOverviewTranslation() throws Exception {
        translationRepository.deleteAll();

        OverviewTranslationDTO patch = new OverviewTranslationDTO();
        patch.setTitle("Ipsum");

        mockMvc.perform(patch(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOverview() throws Exception {
        mockMvc.perform(delete(URL)
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteOverview() throws Exception {
        mockMvc.perform(delete(URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteOverview() throws Exception {
        mockMvc.perform(delete(URL)
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteOverviewTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteOverviewTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteOverviewTranslation() throws Exception {
        mockMvc.perform(delete(URL + "/translations/en-US")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoOverviewFoundShouldThrowItemNotFoundExceptionOnUpdateOverviewOnDeleteOverviewTranslation() throws Exception {
        translationRepository.deleteAll();

        mockMvc.perform(delete(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
