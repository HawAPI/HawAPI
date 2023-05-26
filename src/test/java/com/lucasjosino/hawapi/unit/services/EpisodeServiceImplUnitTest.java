package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import com.lucasjosino.hawapi.services.impl.EpisodeServiceImpl;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertEpisodeEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class EpisodeServiceImplUnitTest {

    private static final EpisodeModel episode = getSingleEpisode();

    @Mock
    private ServiceUtils utils;

    @Mock
    private OpenAPIProperty config;

    @Mock
    private EpisodeRepository episodeRepository;

    @InjectMocks
    private EpisodeServiceImpl episodeService;

    @Test
    public void shouldCreateEpisode() {
        EpisodeModel newEpisode = getNewEpisode();
        when(episodeRepository.save(any(EpisodeModel.class))).thenReturn(newEpisode);

        EpisodeModel res = episodeService.save(newEpisode);

        assertEpisodeEquals(newEpisode, res);
        verify(episodeRepository, times(1)).save(any(EpisodeModel.class));
    }

    @Test
    public void shouldReturnEpisodeByUUID() {
        EpisodeModel newEpisode = getNewEpisode();
        when(episodeRepository.findById(any(UUID.class))).thenReturn(Optional.of(newEpisode));

        EpisodeModel res = episodeService.findByUUID(newEpisode.getUuid());

        assertEpisodeEquals(newEpisode, res);
        verify(episodeRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundEpisode() {
        EpisodeModel newEpisode = getNewEpisode();
        when(episodeRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> episodeService.findByUUID(newEpisode.getUuid()));
        verify(episodeRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfEpisodes() {
        when(episodeRepository.findAll(Mockito.<Example<EpisodeModel>>any())).thenReturn(getEpisodes());

        List<EpisodeModel> res = episodeService.findAll(null);

        assertEquals(2, res.size());
        verify(episodeRepository, times(1)).findAll(Mockito.<Example<EpisodeModel>>any());
    }

    @Test
    public void shouldReturnEmptyListOfEpisodes() {
        when(episodeRepository.findAll(Mockito.<Example<EpisodeModel>>any())).thenReturn(new ArrayList<>());

        List<EpisodeModel> res = episodeService.findAll(null);

        assertEquals(Collections.EMPTY_LIST, res);
        verify(episodeRepository, times(1)).findAll(Mockito.<Example<EpisodeModel>>any());
    }

    @Test
    public void shouldReturnListOfEpisodesWithFilter() {
        List<EpisodeModel> filteredEpisodeList = new ArrayList<>(Collections.singletonList(episode));
        when(episodeRepository.findAll(Mockito.<Example<EpisodeModel>>any())).thenReturn(filteredEpisodeList);

        EpisodeFilter filter = Mockito.mock(EpisodeFilter.class);
        List<EpisodeModel> res = episodeService.findAll(filter);

        assertEquals(1, res.size());
        verify(episodeRepository, times(1)).findAll(Mockito.<Example<EpisodeModel>>any());
    }

    @Test
    public void shouldUpdateEpisode() throws JsonPatchException, JsonProcessingException {
        when(episodeRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getEpisodes().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getEpisodes().get(0));
        when(episodeRepository.save(any(EpisodeModel.class))).thenReturn(getEpisodes().get(0));

        episodeService.patch(episode.getUuid(), mapper().valueToTree(episode));

        verify(episodeRepository, times(1)).save(any(EpisodeModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateEpisode() {
        doThrow(ItemNotFoundException.class)
                .when(episodeRepository).findById(any(UUID.class));

        JsonNode node = mapper().valueToTree(episode);

        assertThrows(ItemNotFoundException.class, () -> episodeService.patch(episode.getUuid(), node));
        verify(episodeRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteEpisode() {
        when(episodeRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(episodeRepository).deleteById(any(UUID.class));

        episodeService.delete(episode.getUuid());

        verify(episodeRepository, times(1)).existsById(any(UUID.class));
        verify(episodeRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteEpisode() {
        when(episodeRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(episodeRepository).deleteById(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> episodeService.delete(episode.getUuid()));
        verify(episodeRepository, times(1)).existsById(any(UUID.class));
        verify(episodeRepository, times(1)).deleteById(any(UUID.class));
    }
}
