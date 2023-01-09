package com.lucasjosino.hawapi.unit.repositories;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertCharacterEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
public class CharacterRepositoryUnitTest extends DatabaseContainerInitializer {

    private static final CharacterModel character = getSingleCharacter();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CharacterRepository characterRepository;

    @BeforeEach
    public void setUp() {
        getCharacters().forEach(entityManager::persist);
    }

    @Test
    public void shouldCreateCharacter() {
        CharacterModel newCharacter = getNewCharacter();
        entityManager.persist(newCharacter);

        CharacterModel res = characterRepository.save(newCharacter);

        assertCharacterEquals(newCharacter, res);
    }

    @Test
    public void shouldReturnCharacterByUUID() {
        Optional<CharacterModel> res = characterRepository.findById(character.getUuid());

        assertTrue(res.isPresent());
        assertCharacterEquals(character, res.get());
    }

    @Test
    public void shouldReturnNotFoundCharacter() {
        entityManager.clear();
        entityManager.flush();

        Optional<CharacterModel> res = characterRepository.findById(character.getUuid());

        assertFalse(res.isPresent());
    }

    @Test
    public void shouldReturnListOfCharacters() {
        List<CharacterModel> res = characterRepository.findAll();

        assertEquals(2, res.size());
    }

    @Test
    public void shouldReturnEmptyListOfCharacters() {
        entityManager.clear();
        entityManager.flush();

        List<CharacterModel> res = characterRepository.findAll();

        assertEquals(Collections.EMPTY_LIST, res);
    }

    @Test
    public void shouldReturnListOfCharactersWithFilter() {
        ModelMapper mapper = new ModelMapper();

        CharacterFilter filter = new CharacterFilter();
        filter.setFirstName("John");

        CharacterModel convertedModel = mapper.map(filter, CharacterModel.class);
        Example<CharacterModel> exFilter = Example.of(convertedModel);
        List<CharacterModel> res = characterRepository.findAll(exFilter);

        assertEquals(1, res.size());
        assertCharacterEquals(character, res.get(0));
    }

    @Test
    public void shouldUpdateCharacter() {
        character.setFirstName("Mario");
        CharacterModel updatedCharacter = characterRepository.save(character);

        assertEquals(character.getUuid(), updatedCharacter.getUuid());
        assertEquals(character.getFirstName(), updatedCharacter.getFirstName());
    }

    @Test
    public void shouldDeleteCharacter() {
        characterRepository.deleteById(character.getUuid());

        Optional<CharacterModel> opCharacter = characterRepository.findById(character.getUuid());

        assertFalse(opCharacter.isPresent());
    }
}
