package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SoundtrackService {

    private final SoundtrackRepository soundtrackRepository;

    private final ServiceUtils utils;

    private final String basePath;

    @Autowired
    public SoundtrackService(SoundtrackRepository soundtrackRepository, ServiceUtils utils, OpenAPIConfig config) {
        this.soundtrackRepository = soundtrackRepository;
        this.utils = utils;
        this.basePath = config.getApiBaseUrl() + "/soundtracks";
    }

    @Transactional
    public List<SoundtrackModel> findAll() {
        return soundtrackRepository.findAll();
    }

    @Transactional
    public SoundtrackModel findByUUID(UUID uuid) {
        Optional<SoundtrackModel> res = soundtrackRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new ItemNotFoundException(SoundtrackModel.class);
    }

    @Transactional
    public SoundtrackModel save(SoundtrackModel soundtrack) {
        UUID soundUUID = UUID.randomUUID();
        soundtrack.setUuid(soundUUID);
        soundtrack.setHref(basePath + "/" + soundUUID);
        return soundtrackRepository.save(soundtrack);
    }

    @Transactional
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException {
        SoundtrackModel season = soundtrackRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        SoundtrackModel patchedSoundtrack = utils.mergePatch(season, patch, SoundtrackModel.class);

        patchedSoundtrack.setUuid(uuid);
        soundtrackRepository.save(patchedSoundtrack);
    }

    @Transactional
    public void delete(UUID uuid) {
        soundtrackRepository.deleteById(uuid);
    }
}
