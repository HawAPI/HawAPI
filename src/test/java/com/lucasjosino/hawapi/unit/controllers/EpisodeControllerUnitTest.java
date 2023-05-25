package com.lucasjosino.hawapi.unit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.EpisodeController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.services.impl.EpisodeServiceImpl;
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

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertEpisodeEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class EpisodeControllerUnitTest {

    private static final EpisodeModel episode = getSingleEpisode();

    @Mock
    private EpisodeServiceImpl episodeService;

    @InjectMocks
    private EpisodeController episodeController;

    @Test
    public void shouldCreateEpisode() {
        EpisodeModel newEpisode = getNewEpisode();
        when(episodeService.save(any(EpisodeModel.class))).thenReturn(newEpisode);

        ResponseEntity<EpisodeModel> res = episodeController.save(newEpisode);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEpisodeEquals(newEpisode, res);
        verify(episodeService, times(1)).save(any(EpisodeModel.class));
    }

    @Test
    public void shouldReturnEpisodeByUUID() {
        EpisodeModel newEpisode = getNewEpisode();
        when(episodeService.findByUUID(any(UUID.class))).thenReturn(newEpisode);

        ResponseEntity<EpisodeModel> res = episodeController.findByUUID(newEpisode.getUuid());

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEpisodeEquals(newEpisode, res);
        verify(episodeService, times(1)).findByUUID(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundEpisode() {
        EpisodeModel newEpisode = getNewEpisode();
        when(episodeService.findByUUID(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> episodeController.findByUUID(newEpisode.getUuid()));
        verify(episodeService, times(1)).findByUUID(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfEpisodes() {
        when(episodeService.findAll(null)).thenReturn(getEpisodes());

        ResponseEntity<List<EpisodeModel>> res = episodeController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(2, res.getBody().size());
        verify(episodeService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnEmptyListOfEpisodes() {
        when(episodeService.findAll(null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<EpisodeModel>> res = episodeController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, res.getBody());
        verify(episodeService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnListOfEpisodesWithFilter() {
        List<EpisodeModel> filteredEpisodeList = new ArrayList<>(Collections.singletonList(episode));
        when(episodeService.findAll(any(EpisodeFilter.class))).thenReturn(filteredEpisodeList);

        EpisodeFilter filter = Mockito.mock(EpisodeFilter.class);
        ResponseEntity<List<EpisodeModel>> res = episodeController.findAll(filter);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(1, res.getBody().size());
        verify(episodeService, times(1)).findAll(any(EpisodeFilter.class));
    }

    @Test
    public void shouldUpdateEpisode() throws JsonPatchException, JsonProcessingException {
        doNothing()
                .when(episodeService).patch(any(UUID.class), any(JsonNode.class));

        ResponseEntity<Void> res = episodeController.patch(episode.getUuid(), mapper().valueToTree(episode));

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(episodeService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateEpisode() throws JsonPatchException, JsonProcessingException {
        doThrow(ItemNotFoundException.class)
                .when(episodeService).patch(any(UUID.class), any(JsonNode.class));

        JsonNode node = mapper().valueToTree(episode);

        assertThrows(ItemNotFoundException.class, () -> episodeController.patch(episode.getUuid(), node));
        verify(episodeService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldDeleteEpisode() {
        doNothing()
                .when(episodeService).delete(any(UUID.class));

        ResponseEntity<Void> res = episodeController.delete(episode.getUuid());

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(episodeService, times(1)).delete(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteEpisode() {
        doThrow(ItemNotFoundException.class)
                .when(episodeService).delete(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> episodeController.delete(episode.getUuid()));
        verify(episodeService, times(1)).delete(any(UUID.class));
    }
}
