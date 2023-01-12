package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.filters.LocationFilter;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertLocationEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.modelMapper;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class LocationRepositoryUnitTest extends DatabaseContainerInitializer {

    private static final LocationModel location = getSingleLocation();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    public void setUp() {
        entityManager.clear();
        entityManager.flush();
        locationRepository.deleteAll();
        getLocations().forEach(entityManager::persist);
    }

    @AfterAll
    public void cleanUp() {
        locationRepository.deleteAll();
    }

    @Test
    public void shouldCreateLocation() {
        LocationModel newLocation = getNewLocation();
        entityManager.persist(newLocation);

        LocationModel res = locationRepository.save(newLocation);

        assertLocationEquals(newLocation, res);
    }

    @Test
    public void shouldReturnLocationByUUID() {
        Optional<LocationModel> res = locationRepository.findById(location.getUuid());

        assertTrue(res.isPresent());
        assertLocationEquals(location, res.get());
    }

    @Test
    public void shouldReturnNotFoundLocation() {
        entityManager.clear();
        entityManager.flush();

        Optional<LocationModel> res = locationRepository.findById(location.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfLocations() {
        List<LocationModel> res = locationRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfLocations() {
        entityManager.clear();
        entityManager.flush();

        List<LocationModel> res = locationRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfLocationsWithFilter() {
        LocationFilter filter = new LocationFilter();
        filter.setName("Lorem");

        LocationModel convertedModel = modelMapper().map(filter, LocationModel.class);
        Example<LocationModel> exFilter = Example.of(convertedModel);
        List<LocationModel> res = locationRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertLocationEquals(location, res.get(0));
    }

    @Test
    public void shouldUpdateLocation() {
        location.setName("New Lorem");
        LocationModel updatedLocation = locationRepository.save(location);

        assertEquals(location.getUuid(), updatedLocation.getUuid());
        assertEquals(location.getName(), updatedLocation.getName());

        location.setName("Lorem");
    }

    @Test
    public void shouldDeleteLocation() {
        locationRepository.deleteById(location.getUuid());

        Optional<LocationModel> opLocation = locationRepository.findById(location.getUuid());

        assertFalse(opLocation.isPresent());
    }
}
