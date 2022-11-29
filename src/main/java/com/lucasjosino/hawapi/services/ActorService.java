package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActorService {

    private final ActorRepository actorRepository;

    @Autowired
    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @Transactional
    public List<ActorModel> findAll() {
        return actorRepository.findAll();
    }

    @Transactional
    public ActorModel findById(UUID uuid) {
        Optional<ActorModel> res = actorRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new NotFoundException("Not Found! UUID: " + uuid);
    }

    @Transactional
    public ActorModel save(ActorModel actor) {
        UUID actorUuid = UUID.randomUUID();
        actor.setUuid(actorUuid);

        if (actor.getSocials() != null && !actor.getSocials().isEmpty()) {
            actor.getSocials().forEach(ac -> ac.setActorUuid(actorUuid));
        }

        return actorRepository.save(actor);
    }

    @Transactional
    public void deleteById(UUID uuid) {
        actorRepository.deleteById(uuid);
    }
}
