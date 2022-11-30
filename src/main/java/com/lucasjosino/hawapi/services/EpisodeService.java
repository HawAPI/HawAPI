package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    private final String basePath;

    @Autowired
    public EpisodeService(EpisodeRepository episodeRepository, OpenAPIConfig config) {
        this.episodeRepository = episodeRepository;
        this.basePath = config.getApiBaseUrl() + "/episodes";
    }

    @Transactional
    public List<EpisodeModel> findAll() {
        return episodeRepository.findAll();
    }

    @Transactional
    public EpisodeModel findByUUID(UUID uuid) {
        Optional<EpisodeModel> res = episodeRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new ItemNotFoundException(EpisodeModel.class);
    }

    @Transactional
    public EpisodeModel save(EpisodeModel episode) {
        UUID episodeUUID = UUID.randomUUID();
        episode.setUuid(episodeUUID);
        episode.setHref(basePath + "/" + episodeUUID);
        return episodeRepository.save(episode);
    }

    @Transactional
    public void delete(UUID uuid) {
        episodeRepository.deleteById(uuid);
    }
}
