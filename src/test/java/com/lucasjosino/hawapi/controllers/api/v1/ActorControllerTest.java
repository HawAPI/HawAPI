package com.lucasjosino.hawapi.controllers.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.security.SecurityConfig;
import com.lucasjosino.hawapi.controllers.advisor.ControllerAdvisor;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.dto.ActorDTO;
import com.lucasjosino.hawapi.models.dto.ActorSocialDTO;
import com.lucasjosino.hawapi.services.impl.ActorServiceImpl;
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
@WebMvcTest(controllers = ActorController.class)
@ContextConfiguration(classes = {ActorController.class, ControllerAdvisor.class, SecurityConfig.class})
class ActorControllerTest {

    private static final String URL = "/api/v1/actors";

    private ActorDTO actor;

    private ActorSocialDTO actorSocial;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ActorServiceImpl service;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        actor = new ActorDTO();
        actor.setUuid(UUID.randomUUID());
        actor.setHref(URL + "/" + actor.getUuid());
        actor.setFirstName("Lorem");
        actor.setLastName("Ipsum");
        actor.setNationality("American");
        actor.setSeasons(Arrays.asList("/api/v1/seasons/1", "/api/v1/seasons/2"));
        actor.setGender((byte) 1);
        actor.setBirthDate(LocalDate.now());
        actor.setCharacter("/api/v1/characters/1");
        actor.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        actor.setSources(Arrays.asList("https://example.com", "https://example.com"));
        actor.setCreatedAt(LocalDateTime.now());
        actor.setUpdatedAt(LocalDateTime.now());

        actorSocial = new ActorSocialDTO();
        actorSocial.setSocial("Lorem");
        actorSocial.setHandle("john_doe");
        actorSocial.setUrl("https://lorem.com/@john_doe");
    }

    @Test
    void shouldReturnAllActors() throws Exception {
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
        when(service.findAll(anyMap(), anyList())).thenReturn(Collections.singletonList(actor));

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
    void shouldReturnAllActorSocials() throws Exception {
        when(service.findAllSocials(any(UUID.class))).thenReturn(Collections.singletonList(actorSocial));

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(service, times(1)).findAllSocials(any(UUID.class));
    }

    @Test
    void whenNoSocialFoundShouldReturnEmptyListOnReturnAllActorSocial() throws Exception {
        when(service.findAllSocials(any(UUID.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).findAllSocials(any(UUID.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnReturnAllActorSocials() throws Exception {
        when(service.findAllSocials(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/" + actor.getUuid() + "/socials"));

        verify(service, times(1)).findAllSocials(any(UUID.class));
    }

    @Test
    void shouldReturnRandomActor() throws Exception {
        when(service.findRandom(nullable(String.class))).thenReturn(actor);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(actor.getUuid())))
                .andExpect(jsonPath("$.href").value(actor.getHref()))
                .andExpect(jsonPath("$.first_name").value(actor.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actor.getLastName()))
                .andExpect(jsonPath("$.nationality").value(actor.getNationality()))
                .andExpect(jsonPath("$.seasons").isNotEmpty())
                .andExpect(jsonPath("$.gender").value(String.valueOf(actor.getGender())))
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(actor.getBirthDate())))
                .andExpect(jsonPath("$.character").value(actor.getCharacter()))
                .andExpect(jsonPath("$.thumbnail").value(actor.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnRandomActor() throws Exception {
        when(service.findRandom(nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/random"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findRandom(nullable(String.class));
    }

    @Test
    void shouldReturnRandomActorSocial() throws Exception {
        when(service.findRandomSocial(any(UUID.class))).thenReturn(actorSocial);

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials/random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.social").value(actorSocial.getSocial()))
                .andExpect(jsonPath("$.handle").value((actorSocial.getHandle())))
                .andExpect(jsonPath("$.url").value(actorSocial.getUrl()));

        verify(service, times(1)).findRandomSocial(any(UUID.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnReturnRandomActorSocial() throws Exception {
        when(service.findRandomSocial(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials/random"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/" + actor.getUuid() + "/socials/random"));

        verify(service, times(1)).findRandomSocial(any(UUID.class));
    }

    @Test
    void whenNoActorSocialFoundShouldThrowItemNotFoundExceptionOnReturnRandomActorSocial() throws Exception {
        when(service.findRandomSocial(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials/random"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/" + actor.getUuid() + "/socials/random"));

        verify(service, times(1)).findRandomSocial(any(UUID.class));
    }

    @Test
    void shouldReturnActorByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenReturn(actor);

        mockMvc.perform(get(URL + "/" + actor.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(actor.getUuid())))
                .andExpect(jsonPath("$.href").value(actor.getHref()))
                .andExpect(jsonPath("$.first_name").value(actor.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actor.getLastName()))
                .andExpect(jsonPath("$.nationality").value(actor.getNationality()))
                .andExpect(jsonPath("$.seasons").isNotEmpty())
                .andExpect(jsonPath("$.gender").value(String.valueOf(actor.getGender())))
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(actor.getBirthDate())))
                .andExpect(jsonPath("$.character").value(actor.getCharacter()))
                .andExpect(jsonPath("$.thumbnail").value(actor.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnActorByUUID() throws Exception {
        when(service.findBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + actor.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).findBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldReturnActorSocialById() throws Exception {
        when(service.findSocialBy(any(UUID.class), anyString())).thenReturn(actorSocial);

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials/Lorem"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.social").value(actorSocial.getSocial()))
                .andExpect(jsonPath("$.handle").value((actorSocial.getHandle())))
                .andExpect(jsonPath("$.url").value(actorSocial.getUrl()));

        verify(service, times(1)).findSocialBy(any(UUID.class), anyString());
    }

    @Test
    void whenNoActorSocialFoundShouldThrowItemNotFoundExceptionOnReturnActorSocialById() throws Exception {
        when(service.findSocialBy(any(UUID.class), nullable(String.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get(URL + "/" + actor.getUuid() + "/socials/Lorem"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.GET.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/" + actor.getUuid() + "/socials/Lorem"));

        verify(service, times(1)).findSocialBy(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldSaveActor() throws Exception {
        when(service.save(any(ActorDTO.class))).thenReturn(actor);

        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actor))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uuid").value(String.valueOf(actor.getUuid())))
                .andExpect(jsonPath("$.href").value(actor.getHref()))
                .andExpect(jsonPath("$.first_name").value(actor.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actor.getLastName()))
                .andExpect(jsonPath("$.nationality").value(actor.getNationality()))
                .andExpect(jsonPath("$.seasons").isNotEmpty())
                .andExpect(jsonPath("$.gender").value(String.valueOf(actor.getGender())))
                .andExpect(jsonPath("$.birth_date").value(String.valueOf(actor.getBirthDate())))
                .andExpect(jsonPath("$.character").value(actor.getCharacter()))
                .andExpect(jsonPath("$.thumbnail").value(actor.getThumbnail()))
                .andExpect(jsonPath("$.sources").isNotEmpty())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(service, times(1)).save(any(ActorDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnSaveActor() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actor))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnSaveActor() throws Exception {
        mockMvc.perform(post(URL)
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actor))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenFieldValidationFailsShouldReturnBadRequestExceptionOnSaveActor() throws Exception {
        actor.setFirstName(null);
        mockMvc.perform(post(URL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actor))
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
    void shouldSaveActorSocial() throws Exception {
        when(service.saveSocial(any(UUID.class), any(ActorSocialDTO.class))).thenReturn(actorSocial);

        mockMvc.perform(post(URL + "/" + actor.getUuid() + "/socials")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actorSocial))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.social").value(actorSocial.getSocial()))
                .andExpect(jsonPath("$.handle").value((actorSocial.getHandle())))
                .andExpect(jsonPath("$.url").value(actorSocial.getUrl()));

        verify(service, times(1)).saveSocial(any(UUID.class), any(ActorSocialDTO.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnSaveActorSocial() throws Exception {
        when(service.saveSocial(any(UUID.class), any(ActorSocialDTO.class))).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(post(URL + "/" + actor.getUuid() + "/socials")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(actorSocial))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.POST.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/" + actor.getUuid() + "/socials"));

        verify(service, times(1)).saveSocial(any(UUID.class), any(ActorSocialDTO.class));
    }

    @Test
    void shouldUpdateActor() throws Exception {
        ActorDTO patch = new ActorDTO();
        patch.setGender((byte) 0);

        doNothing().when(service).patch(any(UUID.class), any(ActorDTO.class));

        mockMvc.perform(patch(URL + "/" + actor.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.gender").value(String.valueOf(patch.getGender())));

        verify(service, times(1)).patch(any(UUID.class), any(ActorDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateActor() throws Exception {
        mockMvc.perform(patch(URL + "/" + actor.getUuid())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateActor() throws Exception {
        mockMvc.perform(patch(URL + "/" + actor.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateActor() throws Exception {
        mockMvc.perform(patch(URL + "/" + actor.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnUpdateActor() throws Exception {
        ActorDTO patch = new ActorDTO();
        patch.setGender((byte) 0);

        doThrow(ItemNotFoundException.class).when(service).patch(any(UUID.class), any(ActorDTO.class));

        mockMvc.perform(patch(URL + "/" + actor.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).patch(any(UUID.class), any(ActorDTO.class));
    }

    @Test
    void shouldUpdateActorSocial() throws Exception {
        ActorSocialDTO patch = new ActorSocialDTO();
        patch.setSocial("Ipsum");

        doNothing().when(service).patchSocial(any(UUID.class), anyString(), any(ActorSocialDTO.class));

        mockMvc.perform(patch(URL + "/" + actor.getUuid() + "/socials/Lorem")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.social").value(patch.getSocial()));

        verify(service, times(1)).patchSocial(any(UUID.class), anyString(), any(ActorSocialDTO.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnUpdateActorSocial() throws Exception {
        mockMvc.perform(patch(URL + "/" + actor.getUuid() + "/socials/Lorem")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnUpdateActorSocial() throws Exception {
        mockMvc.perform(patch(URL + "/" + actor.getUuid())
                        .with(user("dev").roles("DEV"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoBodyShouldReturnBadRequestExceptionOnUpdateActorSocial() throws Exception {
        mockMvc.perform(patch(URL + "/" + actor.getUuid())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoActorSocialFoundShouldThrowItemNotFoundExceptionOnUpdateActorSocial() throws Exception {
        ActorSocialDTO patch = new ActorSocialDTO();
        patch.setSocial("Ipsum");

        doThrow(ItemNotFoundException.class).when(service).patchSocial(any(UUID.class),
                anyString(),
                any(ActorSocialDTO.class)
        );

        mockMvc.perform(patch(URL + "/" + actor.getUuid() + "/socials/Lorem")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patch))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.method").value(HttpMethod.PATCH.name()))
                .andExpect(jsonPath("$.timestamps").exists())
                .andExpect(jsonPath("$.url").value(URL + "/" + actor.getUuid() + "/socials/Lorem"));

        verify(service, times(1)).patchSocial(any(UUID.class), anyString(), any(ActorSocialDTO.class));
    }

    @Test
    void shouldDeleteActor() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + actor.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteActor() throws Exception {
        mockMvc.perform(delete(URL + "/" + actor.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteActor() throws Exception {
        mockMvc.perform(delete(URL + "/" + actor.getUuid())
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnUpdateActorOnDeleteActor() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete(URL + "/" + actor.getUuid())
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldDeleteActorSocial() throws Exception {
        doNothing().when(service).deleteSocial(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + actor.getUuid() + "/socials/Lorem")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteSocial(any(UUID.class), anyString());
    }

    @Test
    void whenNoAuthenticationIsProvidedShouldReturnUnauthorizedExceptionOnDeleteActorSocial() throws Exception {
        mockMvc.perform(delete(URL + "/" + actor.getUuid() + "/socials/Lorem"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthenticationIsProvidedShouldReturnForbiddenExceptionOnDeleteActorSocial() throws Exception {
        mockMvc.perform(delete(URL + "/" + actor.getUuid() + "/socials/Lorem")
                        .with(user("dev").roles("DEV"))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnUpdateActorOnDeleteActorSocial() throws Exception {
        doThrow(ItemNotFoundException.class).when(service).deleteSocial(any(UUID.class), anyString());

        mockMvc.perform(delete(URL + "/" + actor.getUuid() + "/socials/Lorem")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteSocial(any(UUID.class), anyString());
    }
}