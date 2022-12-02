package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SoundtrackService {

    private final SoundtrackRepository soundtrackRepository;

    private final String basePath;

    @Autowired
    public SoundtrackService(SoundtrackRepository soundtrackRepository, OpenAPIConfig config) {
        this.soundtrackRepository = soundtrackRepository;
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
    public void delete(UUID uuid) {
        soundtrackRepository.deleteById(uuid);
    }
}
