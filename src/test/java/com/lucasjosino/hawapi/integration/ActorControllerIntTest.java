package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestConfig
public class ActorControllerIntTest extends DatabaseContainerInitializer {

    private static final ActorModel actor = getSingleActor();

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        actorRepository.deleteAll();
    }

    @Test
    public void shouldCreateActor() throws Exception {
        ActorModel actorToBeSaved = getNewActor();

        mockMvc.perform(post("/api/v1/actors")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.first_name").value(actorToBeSaved.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actorToBeSaved.getLastName()))
                .andExpect(jsonPath("$.gender").value(actorToBeSaved.getGender().toString()))
                .andExpect(jsonPath("$.character").value(actorToBeSaved.getCharacter()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedCreateActor() throws Exception {
        ActorModel actorToBeSaved = getNewActor();

        mockMvc.perform(post("/api/v1/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenCreateActor() throws Exception {
        ActorModel actorToBeSaved = getNewActor();

        mockMvc.perform(post("/api/v1/actors")
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnActorByUUID() throws Exception {
        actorRepository.saveAll(getActors());

        mockMvc.perform(get("/api/v1/actors/" + actor.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(actor.getUuid().toString()))
                .andExpect(jsonPath("$.href").value(actor.getHref()))
                .andExpect(jsonPath("$.first_name").value(actor.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(actor.getLastName()))
                .andExpect(jsonPath("$.gender").value(actor.getGender().toString()))
                .andExpect(jsonPath("$.character").value(actor.getCharacter()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundActor() throws Exception {
        mockMvc.perform(get("/api/v1/actors/" + actor.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfActors() throws Exception {
        actorRepository.saveAll(getActors());

        mockMvc.perform(get("/api/v1/actors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyListOfActors() throws Exception {
        mockMvc.perform(get("/api/v1/actors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnListOfActorsWithFilter() throws Exception {
        ActorModel actor = getActors().get(1);

        actorRepository.saveAll(getActors());

        mockMvc.perform(get("/api/v1/actors")
                        .param("gender", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(actor.getUuid().toString()))
                .andExpect(jsonPath("$[0].href").value(actor.getHref()))
                .andExpect(jsonPath("$[0].first_name").value(actor.getFirstName()))
                .andExpect(jsonPath("$[0].last_name").value(actor.getLastName()))
                .andExpect(jsonPath("$[0].gender").value(actor.getGender().toString()))
                .andExpect(jsonPath("$[0].character").value(actor.getCharacter()))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnListOfActorsWithSortFilter() throws Exception {
        List<ActorModel> reversedActors = new ArrayList<>(getActors());
        Collections.reverse(reversedActors);

        actorRepository.saveAll(getActors());

        mockMvc.perform(get("/api/v1/actors")
                        .param("sort", "DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(reversedActors.get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(reversedActors.get(0).getUuid().toString()));
    }

    @Test
    public void shouldReturnListOfActorsWithOrderFilter() throws Exception {
        actorRepository.saveAll(getActors());

        mockMvc.perform(get("/api/v1/actors")
                        .param("order", "first_name")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(getActors().get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(getActors().get(0).getUuid().toString()));
    }

    @Test
    public void shouldUpdateActor() throws Exception {
        ActorModel actorToBeUpdated = new ActorModel();
        actorToBeUpdated.setLastName("Moa");

        actorRepository.saveAll(getActors());

        mockMvc.perform(patch("/api/v1/actors/" + actor.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundUpdateActor() throws Exception {
        ActorModel actorToBeUpdated = new ActorModel();
        actorToBeUpdated.setLastName("Moa");

        mockMvc.perform(patch("/api/v1/actors/" + actor.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedUpdateActor() throws Exception {
        ActorModel actorToBeUpdated = new ActorModel();
        actorToBeUpdated.setLastName("Moa");

        actorRepository.saveAll(getActors());

        mockMvc.perform(patch("/api/v1/actors/" + actor.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenUpdateActor() throws Exception {
        ActorModel actorToBeUpdated = new ActorModel();
        actorToBeUpdated.setLastName("Moa");

        actorRepository.saveAll(getActors());

        mockMvc.perform(patch("/api/v1/actors/" + actor.getUuid())
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeleteActor() throws Exception {
        actorRepository.saveAll(getActors());

        mockMvc.perform(delete("/api/v1/actors/" + actor.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteActor() throws Exception {
        mockMvc.perform(delete("/api/v1/actors/" + actor.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteActor() throws Exception {
        mockMvc.perform(delete("/api/v1/actors/" + actor.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenDeleteActor() throws Exception {
        mockMvc.perform(delete("/api/v1/actors/" + actor.getUuid())
                        .with(getDevAuth())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
