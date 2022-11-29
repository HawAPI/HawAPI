package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    @Autowired
    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Transactional
    public List<CharacterModel> findAll() {
        return characterRepository.findAll();
    }

    @Transactional
    public CharacterModel findByUUID(UUID uuid) {
        Optional<CharacterModel> res = characterRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new NotFoundException("Not Found! UUID: " + uuid);
    }

    @Transactional
    public CharacterModel save(CharacterModel character) {
        UUID characterUUID = UUID.randomUUID();
        character.setUuid(characterUUID);
        return characterRepository.save(character);
    }

    @Transactional
    public void delete(UUID uuid) {
        characterRepository.deleteById(uuid);
    }
}