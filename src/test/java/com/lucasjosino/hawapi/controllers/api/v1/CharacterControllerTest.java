package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.services.impl.CharacterServiceImpl;
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
@WebMvcTest(controllers = CharacterController.class)
@ContextConfiguration(classes = {CharacterController.class, ControllerAdvisor.class, SecurityConfig.class})
class CharacterControllerTest {

    private static final String URL = "/api/v1/characters";

    private CharacterDTO character;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CharacterServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        character = new CharacterDTO();
        character.setUuid(UUID.randomUUID());
        character.setHref(URL + "/" + character.getUuid());
        character.setFirstName("Lorem");
        character.setLastName("Ipsum");
        character.setNicknames(Arrays.asList("lore", "locum"));
        character.setBirthDate(LocalDate.now());
        character.setDeathDate(LocalDate.now());
        character.setGender((byte) 1);
        character.setActor("/api/v1/actors/1");
        character.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        character.setSources(Arrays.asList("https://example.com", "https://example.com"));
        character.setCreatedAt(LocalDateTime.now());
        character.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldReturnAllCharacters() throws Exception {
        Pageable pageable = Pageable.ofSize(1);
        List<UUID> res = Collections.singletonList(UUID.randomUUID());
        Page<UUID> uuids = PageableExecutionUtils.getPage(res,
                Pageable.ofSize(1),
                () -> 1
        );
        HttpHeaders headers = buildHeaders(pageable, uuids, null);

        when(service.findAllUUIDs(any(Pageable.class), anyLong())).thenReturn(uuids);
        when(responseUtils.getHeaders(any(), any(Pageable.class), nullable(String.class), anyLong()))
                .thenReturn(headers);
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(character));

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Pagination-Page-Index", "1"))
                .andExpect(header().string("X-Pagination-Page-Size", "1"))
                .andExpect(header().string("X-Pagination-Page-Total", "1"))
                .andExpect(header().string("X-Pagination-Item-Total", "1"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
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
    void whenNoUUIDIsFoundShouldReturnEmptyList() throws Exception {
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
    void shouldReturnRandomCharacter() throws Exception {
        when(service.findRandom(nullable(String.class))).thenReturn(character);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(character.getUuid())))
                .andExpect(jsonPath("$.href").value(character.getHref()))
                .andExpect(jsonPath("$.first_name").value(character.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(character.getLastName()))
                .andExpect(jsonPath("$.nicknames").isNotEmpty())
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(character.getBirthDate())))
                .andExpect(jsonPath("$.death_date").value(String.valueOf(character.getDeathDate())))
                .andExpect(jsonPath("$.gender").value(String.valueOf(character.getGender())))
                .andExpect(jsonPath("$.actor").value(character.getActor()))
                .andExpect(jsonPath("$.thumbnail").value(character.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnRandomCharacter() throws Exception {
        when(service.findRandom(nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnCharacterByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenReturn(character);

        mockMvc.perform(get(URL + "/" + character.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(character.getUuid())))
                .andExpect(jsonPath("$.href").value(character.getHref()))
                .andExpect(jsonPath("$.first_name").value(character.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(character.getLastName()))
                .andExpect(jsonPath("$.nicknames").isNotEmpty())
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(character.getBirthDate())))
                .andExpect(jsonPath("$.death_date").value(String.valueOf(character.getDeathDate())))
                .andExpect(jsonPath("$.gender").value(String.valueOf(character.getGender())))
                .andExpect(jsonPath("$.actor").value(character.getActor()))
                .andExpect(jsonPath("$.thumbnail").value(character.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnCharacterByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + character.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldSaveCharacter() throws Exception {
        when(service.save(any(CharacterDTO.class))).thenReturn(character);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(character))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(character.getUuid())))
                .andExpect(jsonPath("$.href").value(character.getHref()))
                .andExpect(jsonPath("$.first_name").value(character.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(character.getLastName()))
                .andExpect(jsonPath("$.nicknames").isNotEmpty())
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(character.getBirthDate())))
                .andExpect(jsonPath("$.death_date").value(String.valueOf(character.getDeathDate())))
                .andExpect(jsonPath("$.gender").value(String.valueOf(character.getGender())))
                .andExpect(jsonPath("$.actor").value(character.getActor()))
                .andExpect(jsonPath("$.thumbnail").value(character.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).save(any(CharacterDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveCharacter() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(character))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveCharacter() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(character))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveCharacter() throws Exception {
        character.setFirstName(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(character))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.message").value("Field 'first_name' is required"))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void shouldUpdateCharacter() throws Exception {
        CharacterDTO patch = new CharacterDTO();
        patch.setGender((byte) 0);

        doNothing().when(service).patch(any(UUID.class), any(CharacterDTO.class));

        mockMvc.perform(patch(URL + "/" + character.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.gender").value(String.valueOf(patch.getGender())));

        verify(service, times(1)).patch(any(UUID.class), any(CharacterDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateCharacter() throws Exception {
        mockMvc.perform(patch(URL + "/" + character.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateCharacter() throws Exception {
        mockMvc.perform(patch(URL + "/" + character.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateCharacter() throws Exception {
        mockMvc.perform(patch(URL + "/" + character.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnUpdateCharacter() throws Exception {
        CharacterDTO patch = new CharacterDTO();
        patch.setGender((byte) 0);

        doThrow(ItemNotFoundException.class).when(service).patch(any(UUID.class), any(CharacterDTO.class));

        mockMvc.perform(patch(URL + "/" + character.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patch(any(UUID.class), any(CharacterDTO.class));
    }

    @Test
    void shouldDeleteCharacter() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + character.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteCharacter() throws Exception {
        mockMvc.perform(delete(URL + "/" + character.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteCharacter() throws Exception {
        mockMvc.perform(delete(URL + "/" + character.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnUpdateCharacterOnDeleteCharacter() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + character.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(any(UUID.class));
    }
}