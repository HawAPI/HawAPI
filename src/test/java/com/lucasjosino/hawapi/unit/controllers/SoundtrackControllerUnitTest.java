package com.lucasjosino.hawapi.unit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.SoundtrackController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SoundtrackFilter;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.services.SoundtrackService;
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

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertSoundtrackEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class SoundtrackControllerUnitTest {

    private static final SoundtrackModel soundtrack = getSingleSoundtrack();

    @Mock
    private SoundtrackService soundtrackService;

    @InjectMocks
    private SoundtrackController soundtrackController;

    @Test
    public void shouldCreateSoundtrack() {
        SoundtrackModel newSoundtrack = getNewSoundtrack();
        when(soundtrackService.save(any(SoundtrackModel.class))).thenReturn(newSoundtrack);

        ResponseEntity<SoundtrackModel> res = soundtrackController.save(newSoundtrack);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertSoundtrackEquals(newSoundtrack, res);
        verify(soundtrackService, times(1)).save(any(SoundtrackModel.class));
    }

    @Test
    public void shouldReturnSoundtrackByUUID() {
        SoundtrackModel newSoundtrack = getNewSoundtrack();
        when(soundtrackService.findByUUID(any(UUID.class))).thenReturn(newSoundtrack);

        ResponseEntity<SoundtrackModel> res = soundtrackController.findByUUID(newSoundtrack.getUuid());

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSoundtrackEquals(newSoundtrack, res);
        verify(soundtrackService, times(1)).findByUUID(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundSoundtrack() {
        SoundtrackModel newSoundtrack = getNewSoundtrack();
        when(soundtrackService.findByUUID(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> soundtrackController.findByUUID(newSoundtrack.getUuid()));
        verify(soundtrackService, times(1)).findByUUID(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfSoundtracks() {
        when(soundtrackService.findAll(null)).thenReturn(getSoundtracks());

        ResponseEntity<List<SoundtrackModel>> res = soundtrackController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(2, res.getBody().size());
        verify(soundtrackService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnEmptyListOfSoundtracks() {
        when(soundtrackService.findAll(null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<SoundtrackModel>> res = soundtrackController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, res.getBody());
        verify(soundtrackService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnListOfSoundtracksWithFilter() {
        List<SoundtrackModel> filteredSoundtrackList = new ArrayList<>(Collections.singletonList(soundtrack));
        when(soundtrackService.findAll(any(SoundtrackFilter.class))).thenReturn(filteredSoundtrackList);

        SoundtrackFilter filter = Mockito.mock(SoundtrackFilter.class);
        ResponseEntity<List<SoundtrackModel>> res = soundtrackController.findAll(filter);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(1, res.getBody().size());
        verify(soundtrackService, times(1)).findAll(any(SoundtrackFilter.class));
    }

    @Test
    public void shouldUpdateSoundtrack() throws JsonPatchException, JsonProcessingException {
        doNothing()
                .when(soundtrackService).patch(any(UUID.class), any(JsonNode.class));

        ResponseEntity<Void> res = soundtrackController.patch(soundtrack.getUuid(), mapper().valueToTree(soundtrack));

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(soundtrackService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateSoundtrack() throws JsonPatchException, JsonProcessingException {
        doThrow(ItemNotFoundException.class)
                .when(soundtrackService).patch(any(UUID.class), any(JsonNode.class));

        JsonNode node = mapper().valueToTree(soundtrack);

        assertThrows(ItemNotFoundException.class, () -> soundtrackController.patch(soundtrack.getUuid(), node));
        verify(soundtrackService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldDeleteSoundtrack() {
        doNothing()
                .when(soundtrackService).delete(any(UUID.class));

        ResponseEntity<Void> res = soundtrackController.delete(soundtrack.getUuid());

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(soundtrackService, times(1)).delete(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteSoundtrack() {
        doThrow(ItemNotFoundException.class)
                .when(soundtrackService).delete(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> soundtrackController.delete(soundtrack.getUuid()));
        verify(soundtrackService, times(1)).delete(any(UUID.class));
    }
}
