package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.LocationFilter;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final ServiceUtils utils;

    private final String basePath;

    @Autowired
    public LocationService(LocationRepository locationRepository, ServiceUtils utils, OpenAPIConfig config) {
        this.locationRepository = locationRepository;
        this.utils = utils;
        this.basePath = config.getApiBaseUrl() + "/places";
    }

    @Transactional
    public List<LocationModel> findAll(LocationFilter filter) {
        Example<LocationModel> filteredModel = utils.filter(filter, LocationModel.class);
        Sort sort = utils.buildSort(filter);

        if (sort == null) return locationRepository.findAll(filteredModel);

        return locationRepository.findAll(filteredModel, sort);
    }

    @Transactional
    public LocationModel findByUUID(UUID uuid) {
        Optional<LocationModel> res = locationRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new ItemNotFoundException(LocationModel.class);
    }

    @Transactional
    public LocationModel save(LocationModel episode) {
        UUID seasonUUID = UUID.randomUUID();
        episode.setUuid(seasonUUID);
        episode.setHref(basePath + "/" + seasonUUID);
        return locationRepository.save(episode);
    }

    @Transactional
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException {
        LocationModel location = locationRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        LocationModel patchedLocation = utils.mergePatch(location, patch, LocationModel.class);

        patchedLocation.setUuid(uuid);
        locationRepository.save(patchedLocation);
    }

    @Transactional
    public void delete(UUID uuid) {
        locationRepository.deleteById(uuid);
    }
}
