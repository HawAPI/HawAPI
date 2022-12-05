package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SeasonService {

    private final SeasonRepository seasonRepository;

    private final ServiceUtils utils;

    private final String basePath;

    @Autowired
    public SeasonService(SeasonRepository seasonRepository, ServiceUtils utils, OpenAPIConfig config) {
        this.seasonRepository = seasonRepository;
        this.utils = utils;
        this.basePath = config.getApiBaseUrl() + "/seasons";
    }

    @Transactional
    public List<SeasonModel> findAll() {
        return seasonRepository.findAll();
    }

    @Transactional
    public SeasonModel findByUUID(UUID uuid) {
        Optional<SeasonModel> res = seasonRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new ItemNotFoundException(SeasonModel.class);
    }

    @Transactional
    public SeasonModel save(SeasonModel season) {
        UUID seasonUUID = UUID.randomUUID();
        season.setUuid(seasonUUID);
        season.setHref(basePath + "/" + seasonUUID);
        return seasonRepository.save(season);
    }

    @Transactional
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException {
        SeasonModel season = seasonRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        SeasonModel patchedLocation = utils.mergePatch(season, patch, SeasonModel.class);

        patchedLocation.setUuid(uuid);
        seasonRepository.save(patchedLocation);
    }

    @Transactional
    public void delete(UUID uuid) {
        seasonRepository.deleteById(uuid);
    }
}
