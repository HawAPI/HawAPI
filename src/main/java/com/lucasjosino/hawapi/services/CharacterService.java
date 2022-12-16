package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
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
public class CharacterService {

    private final CharacterRepository characterRepository;

    private final ServiceUtils utils;

    private final String basePath;

    @Autowired
    public CharacterService(
            CharacterRepository characterRepository,
            ServiceUtils utils,
            OpenAPIProperty config
    ) {
        this.characterRepository = characterRepository;
        this.utils = utils;
        this.basePath = config.getApiBaseUrl() + "/characters";
    }

    @Transactional
    public List<CharacterModel> findAll(CharacterFilter filter) {
        Example<CharacterModel> filteredModel = utils.filter(filter, CharacterModel.class);
        Sort sort = utils.buildSort(filter);

        if (sort == null) return characterRepository.findAll(filteredModel);

        return characterRepository.findAll(filteredModel, sort);
    }

    @Transactional
    public CharacterModel findByUUID(UUID uuid) {
        Optional<CharacterModel> res = characterRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new ItemNotFoundException(CharacterModel.class);
    }

    @Transactional
    public CharacterModel save(CharacterModel character) {
        UUID characterUUID = UUID.randomUUID();
        character.setUuid(characterUUID);
        character.setHref(basePath + "/" + characterUUID);
        return characterRepository.save(character);
    }

    @Transactional
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException {
        CharacterModel character = characterRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        CharacterModel patchedCharacter = utils.mergePatch(character, patch, CharacterModel.class);

        patchedCharacter.setUuid(uuid);
        characterRepository.save(patchedCharacter);
    }

    @Transactional
    public void delete(UUID uuid) {
        characterRepository.deleteById(uuid);
    }
}
