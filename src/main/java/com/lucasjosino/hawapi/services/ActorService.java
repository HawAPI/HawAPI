package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActorService {

    private final ActorRepository actorRepository;

    private final ServiceUtils utils;

    private final String basePath;

    @Autowired
    public ActorService(ActorRepository actorRepository, ServiceUtils utils, OpenAPIProperty config) {
        this.actorRepository = actorRepository;
        this.utils = utils;
        this.basePath = config.getApiBaseUrl() + "/actors";
    }

    @Transactional
    public List<ActorModel> findAll(ActorFilter filter) {
        Example<ActorModel> filteredModel = utils.filter(filter, ActorModel.class);
        Sort sort = utils.buildSort(filter);

        if (sort == null) return actorRepository.findAll(filteredModel);

        return actorRepository.findAll(filteredModel, sort);
    }

    @Transactional
    public ActorModel findById(UUID uuid) {
        return actorRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);
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
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException, ItemNotFoundException {
        ActorModel actor = actorRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        ActorModel patchedActor = utils.mergePatch(actor, patch, ActorModel.class);

        patchedActor.setUuid(uuid);
        actorRepository.save(patchedActor);
    }

    @Transactional
    public void deleteById(UUID uuid) {
        actorRepository.deleteById(uuid);
    }
}
