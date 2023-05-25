package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SoundtrackFilter;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
import com.lucasjosino.hawapi.services.impl.SoundtrackServiceImpl;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertSoundtrackEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class SoundtrackServiceImplUnitTest {

    private static final SoundtrackModel soundtrack = getSingleSoundtrack();

    @Mock
    private ServiceUtils utils;

    @Mock
    private OpenAPIProperty config;

    @Mock
    private SoundtrackRepository soundtrackRepository;

    @InjectMocks
    private SoundtrackServiceImpl soundtrackService;

    @Test
    public void shouldCreateSoundtrack() {
        SoundtrackModel newSoundtrack = getNewSoundtrack();
        when(soundtrackRepository.save(any(SoundtrackModel.class))).thenReturn(newSoundtrack);

        SoundtrackModel res = soundtrackService.save(newSoundtrack);

        assertSoundtrackEquals(newSoundtrack, res);
        verify(soundtrackRepository, times(1)).save(any(SoundtrackModel.class));
    }

    @Test
    public void shouldReturnSoundtrackByUUID() {
        SoundtrackModel newSoundtrack = getNewSoundtrack();
        when(soundtrackRepository.findById(any(UUID.class))).thenReturn(Optional.of(newSoundtrack));

        SoundtrackModel res = soundtrackService.findByUUID(newSoundtrack.getUuid());

        assertSoundtrackEquals(newSoundtrack, res);
        verify(soundtrackRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundSoundtrack() {
        SoundtrackModel newSoundtrack = getNewSoundtrack();
        when(soundtrackRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> soundtrackService.findByUUID(newSoundtrack.getUuid()));
        verify(soundtrackRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfSoundtracks() {
        when(soundtrackRepository.findAll(Mockito.<Example<SoundtrackModel>>any())).thenReturn(getSoundtracks());

        List<SoundtrackModel> res = soundtrackService.findAll(null);

        assertEquals(2, res.size());
        verify(soundtrackRepository, times(1)).findAll(Mockito.<Example<SoundtrackModel>>any());
    }

    @Test
    public void shouldReturnEmptyListOfSoundtracks() {
        when(soundtrackRepository.findAll(Mockito.<Example<SoundtrackModel>>any())).thenReturn(new ArrayList<>());

        List<SoundtrackModel> res = soundtrackService.findAll(null);

        assertEquals(Collections.EMPTY_LIST, res);
        verify(soundtrackRepository, times(1)).findAll(Mockito.<Example<SoundtrackModel>>any());
    }

    @Test
    public void shouldReturnListOfSoundtracksWithFilter() {
        List<SoundtrackModel> filteredSoundtrackList = new ArrayList<>(Collections.singletonList(soundtrack));
        when(soundtrackRepository.findAll(Mockito.<Example<SoundtrackModel>>any())).thenReturn(filteredSoundtrackList);

        SoundtrackFilter filter = Mockito.mock(SoundtrackFilter.class);
        List<SoundtrackModel> res = soundtrackService.findAll(filter);

        assertEquals(1, res.size());
        verify(soundtrackRepository, times(1)).findAll(Mockito.<Example<SoundtrackModel>>any());
    }

    @Test
    public void shouldUpdateSoundtrack() throws JsonPatchException, JsonProcessingException {
        when(soundtrackRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getSoundtracks().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getSoundtracks().get(0));
        when(soundtrackRepository.save(any(SoundtrackModel.class))).thenReturn(getSoundtracks().get(0));

        soundtrackService.patch(soundtrack.getUuid(), mapper().valueToTree(soundtrack));

        verify(soundtrackRepository, times(1)).save(any(SoundtrackModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateSoundtrack() {
        doThrow(ItemNotFoundException.class)
                .when(soundtrackRepository).findById(any(UUID.class));

        JsonNode node = mapper().valueToTree(soundtrack);

        assertThrows(ItemNotFoundException.class, () -> soundtrackService.patch(soundtrack.getUuid(), node));
        verify(soundtrackRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteSoundtrack() {
        when(soundtrackRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(soundtrackRepository).deleteById(any(UUID.class));

        soundtrackService.delete(soundtrack.getUuid());

        verify(soundtrackRepository, times(1)).existsById(any(UUID.class));
        verify(soundtrackRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteSoundtrack() {
        when(soundtrackRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(soundtrackRepository).deleteById(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> soundtrackService.delete(soundtrack.getUuid()));
        verify(soundtrackRepository, times(1)).existsById(any(UUID.class));
        verify(soundtrackRepository, times(1)).deleteById(any(UUID.class));
    }
}
