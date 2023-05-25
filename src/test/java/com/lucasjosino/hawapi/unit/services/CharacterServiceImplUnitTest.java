package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
import com.lucasjosino.hawapi.services.impl.CharacterServiceImpl;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertCharacterEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class CharacterServiceImplUnitTest {

    private static final CharacterModel character = getSingleCharacter();

    @Mock
    private ServiceUtils utils;

    @Mock
    private OpenAPIProperty config;

    @Mock
    private CharacterRepository characterRepository;

    @InjectMocks
    private CharacterServiceImpl characterService;

    @Test
    public void shouldCreateCharacter() {
        CharacterModel newCharacter = getNewCharacter();
        when(characterRepository.save(any(CharacterModel.class))).thenReturn(newCharacter);

        CharacterModel res = characterService.save(newCharacter);

        assertCharacterEquals(newCharacter, res);
        verify(characterRepository, times(1)).save(any(CharacterModel.class));
    }

    @Test
    public void shouldReturnCharacterByUUID() {
        CharacterModel newCharacter = getNewCharacter();
        when(characterRepository.findById(any(UUID.class))).thenReturn(Optional.of(newCharacter));

        CharacterModel res = characterService.findByUUID(newCharacter.getUuid());

        assertCharacterEquals(newCharacter, res);
        verify(characterRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundCharacter() {
        CharacterModel newCharacter = getNewCharacter();
        when(characterRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> characterService.findByUUID(newCharacter.getUuid()));
        verify(characterRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfCharacters() {
        when(characterRepository.findAll(Mockito.<Example<CharacterModel>>any())).thenReturn(getCharacters());

        List<CharacterModel> res = characterService.findAll(null);

        assertEquals(2, res.size());
        verify(characterRepository, times(1)).findAll(Mockito.<Example<CharacterModel>>any());
    }

    @Test
    public void shouldReturnEmptyListOfCharacters() {
        when(characterRepository.findAll(Mockito.<Example<CharacterModel>>any())).thenReturn(new ArrayList<>());

        List<CharacterModel> res = characterService.findAll(null);

        assertEquals(Collections.EMPTY_LIST, res);
        verify(characterRepository, times(1)).findAll(Mockito.<Example<CharacterModel>>any());
    }

    @Test
    public void shouldReturnListOfCharactersWithFilter() {
        List<CharacterModel> filteredCharacterList = new ArrayList<>(Collections.singletonList(character));
        when(characterRepository.findAll(Mockito.<Example<CharacterModel>>any())).thenReturn(filteredCharacterList);

        CharacterFilter filter = Mockito.mock(CharacterFilter.class);
        List<CharacterModel> res = characterService.findAll(filter);

        assertEquals(1, res.size());
        verify(characterRepository, times(1)).findAll(Mockito.<Example<CharacterModel>>any());
    }

    @Test
    public void shouldUpdateCharacter() throws JsonPatchException, JsonProcessingException {
        when(characterRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getCharacters().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getCharacters().get(0));
        when(characterRepository.save(any(CharacterModel.class))).thenReturn(getCharacters().get(0));

        characterService.patch(character.getUuid(), mapper().valueToTree(character));

        verify(characterRepository, times(1)).save(any(CharacterModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateCharacter() {
        doThrow(ItemNotFoundException.class)
                .when(characterRepository).findById(any(UUID.class));

        JsonNode node = mapper().valueToTree(character);

        assertThrows(ItemNotFoundException.class, () -> characterService.patch(character.getUuid(), node));
        verify(characterRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteCharacter() {
        when(characterRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(characterRepository).deleteById(any(UUID.class));

        characterService.delete(character.getUuid());

        verify(characterRepository, times(1)).existsById(any(UUID.class));
        verify(characterRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteCharacter() {
        when(characterRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(characterRepository).deleteById(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> characterService.delete(character.getUuid()));
        verify(characterRepository, times(1)).existsById(any(UUID.class));
        verify(characterRepository, times(1)).deleteById(any(UUID.class));
    }
}
