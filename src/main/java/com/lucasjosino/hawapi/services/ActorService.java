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
    public ActorModel findById(Integer id) {
        Optional<ActorModel> res = actorRepository.findById(id);

        if (res.isPresent()) return res.get();

        throw new NotFoundException("Not Found! UUID: " + id);
    }

    @Transactional
    public ActorModel save(ActorModel actor) {
        actor.setUuid(UUID.randomUUID());
        return actorRepository.save(actor);
    }
}
