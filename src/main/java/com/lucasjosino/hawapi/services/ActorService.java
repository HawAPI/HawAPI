package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActorService {

    private final ActorRepository actorRepository;

    private final String basePath;

    @Autowired
    public ActorService(ActorRepository actorRepository, OpenAPIConfig config) {
        this.actorRepository = actorRepository;
        this.basePath = config.getApiBaseUrl() + "/actors";
    }

    @Transactional
    public List<ActorModel> findAll() {
        return actorRepository.findAll();
    }

    @Transactional
    public ActorModel findById(UUID uuid) {
        Optional<ActorModel> res = actorRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new ItemNotFoundException(ActorModel.class);
    }

    @Transactional
    public ActorModel save(ActorModel actor) {
        UUID actorUUID = UUID.randomUUID();
        actor.setUuid(actorUUID);
        actor.setHref(basePath + "/" + actorUUID);

        if (actor.getSocials() != null && !actor.getSocials().isEmpty()) {
            actor.getSocials().forEach(actorSocial -> actorSocial.setActorUuid(actorUUID));
        }

        return actorRepository.save(actor);
    }

    @Transactional
    public void deleteById(UUID uuid) {
        actorRepository.deleteById(uuid);
    }
}
