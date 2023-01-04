package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.PostgreSQLContainerConfig;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestConfig
public class ActorControllerIntTest extends PostgreSQLContainerConfig {

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
    public void shouldSaveActor() throws Exception {
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
    public void shouldReturnActorList() throws Exception {
        actorRepository.saveAll(getActors());

        mockMvc.perform(get("/api/v1/actors"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
