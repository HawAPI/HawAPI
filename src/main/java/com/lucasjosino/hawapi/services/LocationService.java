package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final String basePath;

    @Autowired
    public LocationService(LocationRepository locationRepository, OpenAPIConfig config) {
        this.locationRepository = locationRepository;
        this.basePath = config.getApiBaseUrl() + "/places";
    }

    @Transactional
    public List<LocationModel> findAll() {
        return locationRepository.findAll();
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
    public void delete(UUID uuid) {
        locationRepository.deleteById(uuid);
    }
}
