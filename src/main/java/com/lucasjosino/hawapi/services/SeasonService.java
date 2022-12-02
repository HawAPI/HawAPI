package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SeasonService {

    private final SeasonRepository seasonRepository;

    private final String basePath;

    @Autowired
    public SeasonService(SeasonRepository seasonRepository, OpenAPIConfig config) {
        this.seasonRepository = seasonRepository;
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
    public void delete(UUID uuid) {
        seasonRepository.deleteById(uuid);
    }
}
