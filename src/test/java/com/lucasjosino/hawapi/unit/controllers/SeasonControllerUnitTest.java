package com.lucasjosino.hawapi.unit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.SeasonController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SeasonFilter;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.services.impl.SeasonServiceImpl;
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

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertSeasonEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class SeasonControllerUnitTest {

    private static final SeasonModel season = getSingleSeason();

    @Mock
    private SeasonServiceImpl seasonService;

    @InjectMocks
    private SeasonController seasonController;

    @Test
    public void shouldCreateSeason() {
        SeasonModel newSeason = getNewSeason();
        when(seasonService.save(any(SeasonModel.class))).thenReturn(newSeason);

        ResponseEntity<SeasonModel> res = seasonController.save(newSeason);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertSeasonEquals(newSeason, res);
        verify(seasonService, times(1)).save(any(SeasonModel.class));
    }

    @Test
    public void shouldReturnSeasonByUUID() {
        SeasonModel newSeason = getNewSeason();
        when(seasonService.findByUUID(any(UUID.class))).thenReturn(newSeason);

        ResponseEntity<SeasonModel> res = seasonController.findByUUID(newSeason.getUuid());

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSeasonEquals(newSeason, res);
        verify(seasonService, times(1)).findByUUID(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundSeason() {
        SeasonModel newSeason = getNewSeason();
        when(seasonService.findByUUID(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> seasonController.findByUUID(newSeason.getUuid()));
        verify(seasonService, times(1)).findByUUID(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfSeasons() {
        when(seasonService.findAll(null)).thenReturn(getSeasons());

        ResponseEntity<List<SeasonModel>> res = seasonController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(2, res.getBody().size());
        verify(seasonService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnEmptyListOfSeasons() {
        when(seasonService.findAll(null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<SeasonModel>> res = seasonController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, res.getBody());
        verify(seasonService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnListOfSeasonsWithFilter() {
        List<SeasonModel> filteredSeasonList = new ArrayList<>(Collections.singletonList(season));
        when(seasonService.findAll(any(SeasonFilter.class))).thenReturn(filteredSeasonList);

        SeasonFilter filter = Mockito.mock(SeasonFilter.class);
        ResponseEntity<List<SeasonModel>> res = seasonController.findAll(filter);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(1, res.getBody().size());
        verify(seasonService, times(1)).findAll(any(SeasonFilter.class));
    }

    @Test
    public void shouldUpdateSeason() throws JsonPatchException, JsonProcessingException {
        doNothing()
                .when(seasonService).patch(any(UUID.class), any(JsonNode.class));

        ResponseEntity<Void> res = seasonController.patch(season.getUuid(), mapper().valueToTree(season));

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(seasonService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateSeason() throws JsonPatchException, JsonProcessingException {
        doThrow(ItemNotFoundException.class)
                .when(seasonService).patch(any(UUID.class), any(JsonNode.class));

        JsonNode node = mapper().valueToTree(season);

        assertThrows(ItemNotFoundException.class, () -> seasonController.patch(season.getUuid(), node));
        verify(seasonService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldDeleteSeason() {
        doNothing()
                .when(seasonService).delete(any(UUID.class));

        ResponseEntity<Void> res = seasonController.delete(season.getUuid());

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(seasonService, times(1)).delete(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteSeason() {
        doThrow(ItemNotFoundException.class)
                .when(seasonService).delete(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> seasonController.delete(season.getUuid()));
        verify(seasonService, times(1)).delete(any(UUID.class));
    }
}
