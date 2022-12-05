package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActorService {

    private final ActorRepository actorRepository;

    private final ObjectMapper mapper;

    private final String basePath;

    @Autowired
    public ActorService(ActorRepository actorRepository, ObjectMapper mapper, OpenAPIConfig config) {
        this.actorRepository = actorRepository;
        this.mapper = mapper;
        this.basePath = config.getApiBaseUrl() + "/actors";
    }

    @Transactional
    public List<ActorModel> findAll() {
        return actorRepository.findAll();
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

        System.out.println(actor.getHref());

        return actorRepository.save(actor);
    }

    @Transactional
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException {
        ActorModel actor = actorRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        JsonNode convertedActor = mapper.valueToTree(actor);
        JsonNode mergedNode = JsonMergePatch.fromJson(patch).apply(convertedActor);
        ActorModel patchedActor = mapper.treeToValue(mergedNode, ActorModel.class);

        patchedActor.setUuid(uuid);
        actorRepository.save(patchedActor);
    }

    @Transactional
    public void deleteById(UUID uuid) {
        actorRepository.deleteById(uuid);
    }
}
