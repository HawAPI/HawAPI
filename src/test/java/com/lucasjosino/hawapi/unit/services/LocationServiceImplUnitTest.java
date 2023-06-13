package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.LocationFilter;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import com.lucasjosino.hawapi.services.impl.LocationServiceImpl;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertLocationEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class LocationServiceImplUnitTest {

    private static final LocationModel location = getSingleLocation();

    @Mock
    private ServiceUtils utils;

    @Mock
    private OpenAPIProperty config;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    public void shouldCreateLocation() {
        LocationModel newLocation = getNewLocation();
        when(locationRepository.save(any(LocationModel.class))).thenReturn(newLocation);

        LocationModel res = locationService.save(newLocation);

        assertLocationEquals(newLocation, res);
        verify(locationRepository, times(1)).save(any(LocationModel.class));
    }

    @Test
    public void shouldReturnLocationByUUID() {
        LocationModel newLocation = getNewLocation();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(newLocation));

        LocationModel res = locationService.findByUUID(newLocation.getUuid());

        assertLocationEquals(newLocation, res);
        verify(locationRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundLocation() {
        LocationModel newLocation = getNewLocation();
        when(locationRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> locationService.findByUUID(newLocation.getUuid()));
        verify(locationRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfLocations() {
        when(locationRepository.findAll(Mockito.<Example<LocationModel>>any())).thenReturn(getLocations());

        List<LocationModel> res = locationService.findAll(null);

        assertEquals(2, res.size());
        verify(locationRepository, times(1)).findAll(Mockito.<Example<LocationModel>>any());
    }

    @Test
    public void shouldReturnEmptyListOfLocations() {
        when(locationRepository.findAll(Mockito.<Example<LocationModel>>any())).thenReturn(new ArrayList<>());

        List<LocationModel> res = locationService.findAll(null);

        assertEquals(Collections.EMPTY_LIST, res);
        verify(locationRepository, times(1)).findAll(Mockito.<Example<LocationModel>>any());
    }

    @Test
    public void shouldReturnListOfLocationsWithFilter() {
        List<LocationModel> filteredLocationList = new ArrayList<>(Collections.singletonList(location));
        when(locationRepository.findAll(Mockito.<Example<LocationModel>>any())).thenReturn(filteredLocationList);

        LocationFilter filter = Mockito.mock(LocationFilter.class);
        List<LocationModel> res = locationService.findAll(filter);

        assertEquals(1, res.size());
        verify(locationRepository, times(1)).findAll(Mockito.<Example<LocationModel>>any());
    }

    @Test
    public void shouldUpdateLocation() throws JsonPatchException, JsonProcessingException {
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getLocations().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getLocations().get(0));
        when(locationRepository.save(any(LocationModel.class))).thenReturn(getLocations().get(0));

        locationService.patch(location.getUuid(), mapper().valueToTree(location));

        verify(locationRepository, times(1)).save(any(LocationModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateLocation() {
        doThrow(ItemNotFoundException.class)
                .when(locationRepository).findById(any(UUID.class));

        JsonNode node = mapper().valueToTree(location);

        assertThrows(ItemNotFoundException.class, () -> locationService.patch(location.getUuid(), node));
        verify(locationRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteLocation() {
        when(locationRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(locationRepository).deleteById(any(UUID.class));

        locationService.delete(location.getUuid());

        verify(locationRepository, times(1)).existsById(any(UUID.class));
        verify(locationRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteLocation() {
        when(locationRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(locationRepository).deleteById(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> locationService.delete(location.getUuid()));
        verify(locationRepository, times(1)).existsById(any(UUID.class));
        verify(locationRepository, times(1)).deleteById(any(UUID.class));
    }
}
