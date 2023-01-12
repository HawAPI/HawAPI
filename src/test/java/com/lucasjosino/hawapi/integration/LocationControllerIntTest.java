package com.lucasjosino.hawapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.configs.IntegrationTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import org.junit.jupiter.api.AfterAll;
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
public class LocationControllerIntTest extends DatabaseContainerInitializer {

    private static final LocationModel location = getSingleLocation();

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void clearDatabase() {
        locationRepository.deleteAll();
    }

    @AfterAll
    public void cleanUp() {
        locationRepository.deleteAll();
    }

    @Test
    public void shouldCreateLocation() throws Exception {
        LocationModel locationToBeSaved = getNewLocation();

        mockMvc.perform(post("/api/v1/locations")
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(locationToBeSaved.getName()))
                .andExpect(jsonPath("$.description").value(locationToBeSaved.getDescription()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnUnauthorizedCreateLocation() throws Exception {
        LocationModel locationToBeSaved = getNewLocation();

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenCreateLocation() throws Exception {
        LocationModel locationToBeSaved = getNewLocation();

        mockMvc.perform(post("/api/v1/locations")
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationToBeSaved))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnLocationByUUID() throws Exception {
        locationRepository.saveAll(getLocations());

        mockMvc.perform(get("/api/v1/locations/" + location.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(location.getUuid().toString()))
                .andExpect(jsonPath("$.href").value(location.getHref()))
                .andExpect(jsonPath("$.name").value(location.getName()))
                .andExpect(jsonPath("$.description").value(location.getDescription()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnNotFoundLocation() throws Exception {
        mockMvc.perform(get("/api/v1/locations/" + location.getUuid()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfLocations() throws Exception {
        locationRepository.saveAll(getLocations());

        mockMvc.perform(get("/api/v1/locations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyListOfLocations() throws Exception {
        mockMvc.perform(get("/api/v1/locations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnListOfLocationsWithFilter() throws Exception {
        LocationModel location = getLocations().get(1);

        locationRepository.saveAll(getLocations());

        mockMvc.perform(get("/api/v1/locations")
                        .param("name", "Ipsum")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(location.getUuid().toString()))
                .andExpect(jsonPath("$[0].href").value(location.getHref()))
                .andExpect(jsonPath("$[0].name").value(location.getName()))
                .andExpect(jsonPath("$[0].description").value(location.getDescription()))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());
    }

    @Test
    public void shouldReturnListOfLocationsWithSortFilter() throws Exception {
        List<LocationModel> reversedLocations = new ArrayList<>(getLocations());
        Collections.reverse(reversedLocations);

        locationRepository.saveAll(getLocations());

        mockMvc.perform(get("/api/v1/locations")
                        .param("sort", "DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(reversedLocations.get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(reversedLocations.get(0).getUuid().toString()));
    }

    @Test
    public void shouldReturnListOfLocationsWithOrderFilter() throws Exception {
        locationRepository.saveAll(getLocations());

        mockMvc.perform(get("/api/v1/locations")
                        .param("order", "name")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid").value(getLocations().get(1).getUuid().toString()))
                .andExpect(jsonPath("$[1].uuid").value(getLocations().get(0).getUuid().toString()));
    }

    @Test
    public void shouldUpdateLocation() throws Exception {
        LocationModel locationToBeUpdated = new LocationModel();
        locationToBeUpdated.setName("Moa");

        locationRepository.saveAll(getLocations());

        mockMvc.perform(patch("/api/v1/locations/" + location.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundUpdateLocation() throws Exception {
        LocationModel locationToBeUpdated = new LocationModel();
        locationToBeUpdated.setName("Moa");

        mockMvc.perform(patch("/api/v1/locations/" + location.getUuid())
                        .with(getAdminAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedUpdateLocation() throws Exception {
        LocationModel locationToBeUpdated = new LocationModel();
        locationToBeUpdated.setName("Moa");

        locationRepository.saveAll(getLocations());

        mockMvc.perform(patch("/api/v1/locations/" + location.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenUpdateLocation() throws Exception {
        LocationModel locationToBeUpdated = new LocationModel();
        locationToBeUpdated.setName("Moa");

        locationRepository.saveAll(getLocations());

        mockMvc.perform(patch("/api/v1/locations/" + location.getUuid())
                        .with(getDevAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationToBeUpdated))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeleteLocation() throws Exception {
        locationRepository.saveAll(getLocations());

        mockMvc.perform(delete("/api/v1/locations/" + location.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundDeleteLocation() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/" + location.getUuid())
                        .with(getAdminAuth())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnauthorizedDeleteLocation() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/" + location.getUuid()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnForbiddenDeleteLocation() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/" + location.getUuid())
                        .with(getDevAuth())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
