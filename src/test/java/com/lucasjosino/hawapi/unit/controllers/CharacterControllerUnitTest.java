package com.lucasjosino.hawapi.unit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.CharacterController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.services.CharacterService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertCharacterEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class CharacterControllerUnitTest {

    private static final CharacterModel character = getSingleCharacter();

    @Mock
    private CharacterService characterService;

    @InjectMocks
    private CharacterController characterController;

    @Test
    public void shouldCreateCharacter() {
        CharacterModel newCharacter = getNewCharacter();
        when(characterService.save(any(CharacterModel.class))).thenReturn(newCharacter);

        ResponseEntity<CharacterModel> res = characterController.save(newCharacter);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertCharacterEquals(newCharacter, res);
        verify(characterService, times(1)).save(any(CharacterModel.class));
    }

    @Test
    public void shouldReturnCharacterByUUID() {
        CharacterModel newCharacter = getNewCharacter();
        when(characterService.findByUUID(any(UUID.class))).thenReturn(newCharacter);

        ResponseEntity<CharacterModel> res = characterController.findByUUID(newCharacter.getUuid());

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertCharacterEquals(newCharacter, res);
        verify(characterService, times(1)).findByUUID(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundCharacter() {
        CharacterModel newCharacter = getNewCharacter();
        when(characterService.findByUUID(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> characterController.findByUUID(newCharacter.getUuid()));
        verify(characterService, times(1)).findByUUID(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfCharacters() {
        when(characterService.findAll(null)).thenReturn(getCharacters());

        ResponseEntity<List<CharacterModel>> res = characterController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(2, res.getBody().size());
        verify(characterService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnEmptyListOfCharacters() {
        when(characterService.findAll(null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<CharacterModel>> res = characterController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, res.getBody());
        verify(characterService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnListOfCharactersWithFilter() {
        List<CharacterModel> filteredCharacterList = new ArrayList<>(Collections.singletonList(character));
        when(characterService.findAll(any(CharacterFilter.class))).thenReturn(filteredCharacterList);

        CharacterFilter filter = Mockito.mock(CharacterFilter.class);
        ResponseEntity<List<CharacterModel>> res = characterController.findAll(filter);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(1, res.getBody().size());
        verify(characterService, times(1)).findAll(any(CharacterFilter.class));
    }

    @Test
    public void shouldUpdateCharacter() throws JsonPatchException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        doNothing()
                .when(characterService).patch(any(UUID.class), any(JsonNode.class));

        ResponseEntity<Void> res = characterController.patch(character.getUuid(), mapper.valueToTree(character));

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(characterService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateCharacter() throws JsonPatchException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        doThrow(ItemNotFoundException.class)
                .when(characterService).patch(any(UUID.class), any(JsonNode.class));

        JsonNode node = mapper.valueToTree(character);

        assertThrows(ItemNotFoundException.class, () -> characterController.patch(character.getUuid(), node));
        verify(characterService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldDeleteCharacter() {
        doNothing()
                .when(characterService).delete(any(UUID.class));

        ResponseEntity<Void> res = characterController.delete(character.getUuid());

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(characterService, times(1)).delete(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteCharacter() {
        doThrow(ItemNotFoundException.class)
                .when(characterService).delete(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> characterController.delete(character.getUuid()));
        verify(characterService, times(1)).delete(any(UUID.class));
    }
}
