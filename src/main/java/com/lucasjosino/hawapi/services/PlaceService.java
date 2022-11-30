package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.models.PlaceModel;
import com.lucasjosino.hawapi.repositories.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final String basePath;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, OpenAPIConfig config) {
        this.placeRepository = placeRepository;
        this.basePath = config.getApiBaseUrl() + "/seasons";
    }

    @Transactional
    public List<PlaceModel> findAll() {
        return placeRepository.findAll();
    }

    @Transactional
    public PlaceModel findByUUID(UUID uuid) {
        Optional<PlaceModel> res = placeRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new NotFoundException("Not Found! UUID: " + uuid);
    }

    @Transactional
    public PlaceModel save(PlaceModel episode) {
        UUID seasonUUID = UUID.randomUUID();
        episode.setUuid(seasonUUID);
        episode.setHref(basePath + seasonUUID);
        return placeRepository.save(episode);
    }

    @Transactional
    public void delete(UUID uuid) {
        placeRepository.deleteById(uuid);
    }
}
