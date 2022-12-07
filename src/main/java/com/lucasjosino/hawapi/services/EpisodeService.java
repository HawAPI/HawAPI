package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
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
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    private final ServiceUtils utils;

    private final String basePath;

    @Autowired
    public EpisodeService(EpisodeRepository episodeRepository, ServiceUtils utils, OpenAPIConfig config) {
        this.episodeRepository = episodeRepository;
        this.utils = utils;
        this.basePath = config.getApiBaseUrl() + "/episodes";
    }

    @Transactional
    public List<EpisodeModel> findAll(EpisodeFilter filter) {
        Example<EpisodeModel> filteredModel = utils.filter(filter, EpisodeModel.class);
        Sort sort = utils.buildSort(filter);

        if (sort == null) return episodeRepository.findAll(filteredModel);

        return episodeRepository.findAll(filteredModel, sort);
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
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException {
        EpisodeModel episode = episodeRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        EpisodeModel patchedEpisode = utils.mergePatch(episode, patch, EpisodeModel.class);

        patchedEpisode.setUuid(uuid);
        episodeRepository.save(patchedEpisode);
    }

    @Transactional
    public void delete(UUID uuid) {
        episodeRepository.deleteById(uuid);
    }
}
