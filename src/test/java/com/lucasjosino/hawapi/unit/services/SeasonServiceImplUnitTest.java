package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SeasonFilter;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import com.lucasjosino.hawapi.services.impl.SeasonServiceImpl;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertSeasonEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class SeasonServiceImplUnitTest {

    private static final SeasonModel season = getSingleSeason();

    @Mock
    private ServiceUtils utils;

    @Mock
    private OpenAPIProperty config;

    @Mock
    private SeasonRepository seasonRepository;

    @InjectMocks
    private SeasonServiceImpl seasonService;

    @Test
    public void shouldCreateSeason() {
        SeasonModel newSeason = getNewSeason();
        when(seasonRepository.save(any(SeasonModel.class))).thenReturn(newSeason);

        SeasonModel res = seasonService.save(newSeason);

        assertSeasonEquals(newSeason, res);
        verify(seasonRepository, times(1)).save(any(SeasonModel.class));
    }

    @Test
    public void shouldReturnSeasonByUUID() {
        SeasonModel newSeason = getNewSeason();
        when(seasonRepository.findById(any(UUID.class))).thenReturn(Optional.of(newSeason));

        SeasonModel res = seasonService.findByUUID(newSeason.getUuid());

        assertSeasonEquals(newSeason, res);
        verify(seasonRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundSeason() {
        SeasonModel newSeason = getNewSeason();
        when(seasonRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> seasonService.findByUUID(newSeason.getUuid()));
        verify(seasonRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfSeasons() {
        when(seasonRepository.findAll(Mockito.<Example<SeasonModel>>any())).thenReturn(getSeasons());

        List<SeasonModel> res = seasonService.findAll(null);

        assertEquals(2, res.size());
        verify(seasonRepository, times(1)).findAll(Mockito.<Example<SeasonModel>>any());
    }

    @Test
    public void shouldReturnEmptyListOfSeasons() {
        when(seasonRepository.findAll(Mockito.<Example<SeasonModel>>any())).thenReturn(new ArrayList<>());

        List<SeasonModel> res = seasonService.findAll(null);

        assertEquals(Collections.EMPTY_LIST, res);
        verify(seasonRepository, times(1)).findAll(Mockito.<Example<SeasonModel>>any());
    }

    @Test
    public void shouldReturnListOfSeasonsWithFilter() {
        List<SeasonModel> filteredSeasonList = new ArrayList<>(Collections.singletonList(season));
        when(seasonRepository.findAll(Mockito.<Example<SeasonModel>>any())).thenReturn(filteredSeasonList);

        SeasonFilter filter = Mockito.mock(SeasonFilter.class);
        List<SeasonModel> res = seasonService.findAll(filter);

        assertEquals(1, res.size());
        verify(seasonRepository, times(1)).findAll(Mockito.<Example<SeasonModel>>any());
    }

    @Test
    public void shouldUpdateSeason() throws JsonPatchException, JsonProcessingException {
        when(seasonRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getSeasons().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getSeasons().get(0));
        when(seasonRepository.save(any(SeasonModel.class))).thenReturn(getSeasons().get(0));

        seasonService.patch(season.getUuid(), mapper().valueToTree(season));

        verify(seasonRepository, times(1)).save(any(SeasonModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateSeason() {
        doThrow(ItemNotFoundException.class)
                .when(seasonRepository).findById(any(UUID.class));

        JsonNode node = mapper().valueToTree(season);

        assertThrows(ItemNotFoundException.class, () -> seasonService.patch(season.getUuid(), node));
        verify(seasonRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteSeason() {
        when(seasonRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(seasonRepository).deleteById(any(UUID.class));

        seasonService.delete(season.getUuid());

        verify(seasonRepository, times(1)).existsById(any(UUID.class));
        verify(seasonRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteSeason() {
        when(seasonRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(seasonRepository).deleteById(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> seasonService.delete(season.getUuid()));
        verify(seasonRepository, times(1)).existsById(any(UUID.class));
        verify(seasonRepository, times(1)).deleteById(any(UUID.class));
    }
}
