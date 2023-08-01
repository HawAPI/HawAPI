package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import com.lucasjosino.hawapi.models.dto.translation.OverviewTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.services.impl.OverviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.UUID;

import static com.lucasjosino.hawapi.utils.TestUtils.buildHeaders;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = OverviewController.class)
@ContextConfiguration(
        classes = {
                OverviewController.class,
                ControllerAdvisor.class,
                SecurityConfig.class,
                OpenAPIProperty.class
        }
)
class OverviewControllerTest {

    private static final String URL = "/api/v1/overview";

    private OverviewDTO overview;

    private OverviewTranslationDTO translation;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OverviewServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @Autowired
    private OpenAPIProperty apiConfig;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        overview = new OverviewDTO();
        overview.setUuid(UUID.randomUUID());
        overview.setHref("/api/v1/overview/" + overview.getUuid());
        overview.setCreators(Collections.singletonList("Lorem"));
        overview.setLanguages(Collections.singletonList("en-US"));
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
    }

    @Test
    void shouldReturnAllOverviewTranslations() throws Exception {
        when(service.findAllOverviewTranslations()).thenReturn(Collections.singletonList(translation));

        mockMvc.perform(get(URL + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(service, times(1)).findAllOverviewTranslations();
    }

    @Test
    void whenNoTranslationFoundShouldReturnEmptyListOnReturnAllOverviewTranslations() throws Exception {
        when(service.findAllOverviewTranslations()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL + "/translations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).findAllOverviewTranslations();
    }

    @Test
    void shouldReturnOverview() throws Exception {
        when(service.findOverviewBy(anyString())).thenReturn(overview);

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
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findOverviewBy(anyString());
    }

    @Test
    void shouldReturnOverviewWithDefaultLanguage() throws Exception {
        when(responseUtils.getDefaultLanguage()).thenReturn("en-US");
        when(service.findOverviewBy(anyString())).thenReturn(overview);

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
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(responseUtils, times(1)).getDefaultLanguage();
        verify(service, times(1)).findOverviewBy(anyString());
    }

    @Test
    void whenNoOverviewFoundShouldReturnItemNotFoundExceptionOnReturnOverview() throws Exception {
        when(responseUtils.getDefaultLanguage()).thenReturn("en-US");
        when(service.findOverviewBy(anyString())).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));

        verify(responseUtils, times(1)).getDefaultLanguage();
        verify(service, times(1)).findOverviewBy(anyString());
    }

    @Test
    void shouldReturnOverviewTranslationBy() throws Exception {
        HttpHeaders headers = buildHeaders("en-US");

        when(service.findOverviewTranslationBy(anyString())).thenReturn(translation);
        when(responseUtils.getHeaders(anyString())).thenReturn(headers);

        mockMvc.perform(get(URL + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.title").value(overview.getTitle()))
                .andExpect(jsonPath("$.description").value(overview.getDescription()))
                .andExpect(jsonPath("$.language").value(overview.getLanguage()));

        verify(service, times(1)).findOverviewTranslationBy(anyString());
        verify(responseUtils, times(1)).getHeaders(anyString());
    }

    @Test
    void whenNoOverviewFoundShouldReturnItemNotFoundExceptionOnReturnOverviewTranslationBy() throws Exception {
        when(service.findOverviewTranslationBy(anyString())).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/translations/en-US"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/translations/en-US"));

        verify(service, times(1)).findOverviewTranslationBy(anyString());
    }

    @Test
    void shouldSaveOverview() throws Exception {
        HttpHeaders headers = buildHeaders("en-US");

        when(service.saveOverview(any(OverviewDTO.class))).thenReturn(overview);
        when(responseUtils.getHeaders(anyString())).thenReturn(headers);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(overview))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Content-Language", "en-US"))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(overview.getUuid())))
                .andExpect(jsonPath("$.href").value(overview.getHref()))
                .andExpect(jsonPath("$.title").value(overview.getTitle()))
                .andExpect(jsonPath("$.description").value(overview.getDescription()))
                .andExpect(jsonPath("$.thumbnail").value(overview.getThumbnail()))
                .andExpect(jsonPath("$.language").value(overview.getLanguage()))
                .andExpect(jsonPath("$.creators").isNotEmpty())
                .andExpect(jsonPath("$.languages").isNotEmpty())
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).saveOverview(any(OverviewDTO.class));
        verify(responseUtils, times(1)).getHeaders(anyString());
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
        HttpHeaders headers = buildHeaders("en-US");

        when(responseUtils.getDefaultLanguage()).thenReturn("en-US");
        when(service.saveOverviewTranslation(anyString(), any(OverviewTranslationDTO.class))).thenReturn(translation);
        when(responseUtils.getHeaders(anyString())).thenReturn(headers);

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

        verify(responseUtils, times(1)).getDefaultLanguage();
        verify(service, times(1))
                .saveOverviewTranslation(anyString(), any(OverviewTranslationDTO.class));
        verify(responseUtils, times(1)).getHeaders(anyString());
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

        doNothing().when(service).patchOverview(any(OverviewDTO.class));

        mockMvc.perform(patch(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.thumbnail").value(String.valueOf(patch.getThumbnail())));

        verify(service, times(1)).patchOverview(any(OverviewDTO.class));
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
        OverviewDTO patch = new OverviewDTO();
        patch.setThumbnail("https://example.com/image.jpg");

        doThrow(ItemNotFoundException.class).when(service).patchOverview(any(OverviewDTO.class));

        mockMvc.perform(patch(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patchOverview(any(OverviewDTO.class));
    }

    @Test
    void shouldUpdateOverviewTranslation() throws Exception {
        HttpHeaders headers = buildHeaders("en-US");
        OverviewTranslationDTO patch = new OverviewTranslationDTO();
        patch.setTitle("Lorem");

        when(responseUtils.getHeaders(anyString())).thenReturn(headers);
        doNothing().when(service).patchOverviewTranslation(anyString(), any(OverviewTranslationDTO.class));

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

        verify(responseUtils, times(1)).getHeaders(anyString());
        verify(service, times(1))
                .patchOverviewTranslation(anyString(), any(OverviewTranslationDTO.class));
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
        OverviewTranslationDTO patch = new OverviewTranslationDTO();
        patch.setTitle("Ipsum");

        doThrow(ItemNotFoundException.class).when(service).patchOverviewTranslation(
                anyString(),
                any(OverviewTranslationDTO.class)
        );

        mockMvc.perform(patch(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patchOverviewTranslation(
                anyString(),
                any(OverviewTranslationDTO.class)
        );
    }

    @Test
    void shouldDeleteOverview() throws Exception {
        doNothing().when(service).deleteOverview();

        mockMvc.perform(delete(URL)
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteOverview();
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
        doNothing().when(service).deleteOverviewTranslation(anyString());

        mockMvc.perform(delete(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteOverviewTranslation(anyString());
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
        doThrow(ItemNotFoundException.class).when(service).deleteOverviewTranslation(anyString());

        mockMvc.perform(delete(URL + "/translations/en-US")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteOverviewTranslation(anyString());
    }
}