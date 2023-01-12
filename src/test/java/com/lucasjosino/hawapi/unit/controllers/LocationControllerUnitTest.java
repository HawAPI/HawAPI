package com.lucasjosino.hawapi.unit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.LocationController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.LocationFilter;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.services.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertLocationEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class LocationControllerUnitTest {

    private static final LocationModel location = getSingleLocation();

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    @Test
    public void shouldCreateLocation() {
        LocationModel newLocation = getNewLocation();
        when(locationService.save(any(LocationModel.class))).thenReturn(newLocation);

        ResponseEntity<LocationModel> res = locationController.save(newLocation);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertLocationEquals(newLocation, res);
        verify(locationService, times(1)).save(any(LocationModel.class));
    }

    @Test
    public void shouldReturnLocationByUUID() {
        LocationModel newLocation = getNewLocation();
        when(locationService.findByUUID(any(UUID.class))).thenReturn(newLocation);

        ResponseEntity<LocationModel> res = locationController.findByUUID(newLocation.getUuid());

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertLocationEquals(newLocation, res);
        verify(locationService, times(1)).findByUUID(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundLocation() {
        LocationModel newLocation = getNewLocation();
        when(locationService.findByUUID(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> locationController.findByUUID(newLocation.getUuid()));
        verify(locationService, times(1)).findByUUID(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfLocations() {
        when(locationService.findAll(null)).thenReturn(getLocations());

        ResponseEntity<List<LocationModel>> res = locationController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(2, res.getBody().size());
        verify(locationService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnEmptyListOfLocations() {
        when(locationService.findAll(null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<LocationModel>> res = locationController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, res.getBody());
        verify(locationService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnListOfLocationsWithFilter() {
        List<LocationModel> filteredLocationList = new ArrayList<>(Collections.singletonList(location));
        when(locationService.findAll(any(LocationFilter.class))).thenReturn(filteredLocationList);

        LocationFilter filter = Mockito.mock(LocationFilter.class);
        ResponseEntity<List<LocationModel>> res = locationController.findAll(filter);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(1, res.getBody().size());
        verify(locationService, times(1)).findAll(any(LocationFilter.class));
    }

    @Test
    public void shouldUpdateLocation() throws JsonPatchException, JsonProcessingException {
        doNothing()
                .when(locationService).patch(any(UUID.class), any(JsonNode.class));

        ResponseEntity<Void> res = locationController.patch(location.getUuid(), mapper().valueToTree(location));

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(locationService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateLocation() throws JsonPatchException, JsonProcessingException {
        doThrow(ItemNotFoundException.class)
                .when(locationService).patch(any(UUID.class), any(JsonNode.class));

        JsonNode node = mapper().valueToTree(location);

        assertThrows(ItemNotFoundException.class, () -> locationController.patch(location.getUuid(), node));
        verify(locationService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldDeleteLocation() {
        doNothing()
                .when(locationService).delete(any(UUID.class));

        ResponseEntity<Void> res = locationController.delete(location.getUuid());

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(locationService, times(1)).delete(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteLocation() {
        doThrow(ItemNotFoundException.class)
                .when(locationService).delete(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> locationController.delete(location.getUuid()));
        verify(locationService, times(1)).delete(any(UUID.class));
    }
}
