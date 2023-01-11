package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import com.lucasjosino.hawapi.services.ActorService;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertActorEquals;
import static com.lucasjosino.hawapi.utils.TestsData.*;
import static com.lucasjosino.hawapi.utils.TestsUtils.mapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class ActorServiceUnitTest {

    private static final ActorModel actor = getSingleActor();

    @Mock
    private ServiceUtils utils;

    @Mock
    private OpenAPIProperty config;

    @Mock
    private ActorRepository actorRepository;

    @InjectMocks
    private ActorService actorService;

    @Test
    public void shouldCreateActor() {
        ActorModel newActor = getNewActor();
        when(actorRepository.save(any(ActorModel.class))).thenReturn(newActor);

        ActorModel res = actorService.save(newActor);

        assertActorEquals(newActor, res);
        verify(actorRepository, times(1)).save(any(ActorModel.class));
    }

    @Test
    public void shouldReturnActorByUUID() {
        ActorModel newActor = getNewActor();
        when(actorRepository.findById(any(UUID.class))).thenReturn(Optional.of(newActor));

        ActorModel res = actorService.findById(newActor.getUuid());

        assertActorEquals(newActor, res);
        verify(actorRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundActor() {
        ActorModel newActor = getNewActor();
        when(actorRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> actorService.findById(newActor.getUuid()));
        verify(actorRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfActors() {
        when(actorRepository.findAll(Mockito.<Example<ActorModel>>any())).thenReturn(getActors());

        List<ActorModel> res = actorService.findAll(null);

        assertEquals(2, res.size());
        verify(actorRepository, times(1)).findAll(Mockito.<Example<ActorModel>>any());
    }

    @Test
    public void shouldReturnEmptyListOfActors() {
        when(actorRepository.findAll(Mockito.<Example<ActorModel>>any())).thenReturn(new ArrayList<>());

        List<ActorModel> res = actorService.findAll(null);

        assertEquals(Collections.EMPTY_LIST, res);
        verify(actorRepository, times(1)).findAll(Mockito.<Example<ActorModel>>any());
    }

    @Test
    public void shouldReturnListOfActorsWithFilter() {
        List<ActorModel> filteredActorList = new ArrayList<>(Collections.singletonList(actor));
        when(actorRepository.findAll(Mockito.<Example<ActorModel>>any())).thenReturn(filteredActorList);

        ActorFilter filter = Mockito.mock(ActorFilter.class);
        List<ActorModel> res = actorService.findAll(filter);

        assertEquals(1, res.size());
        verify(actorRepository, times(1)).findAll(Mockito.<Example<ActorModel>>any());
    }

    @Test
    public void shouldUpdateActor() throws JsonPatchException, JsonProcessingException {
        when(actorRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getActors().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getActors().get(0));
        when(actorRepository.save(any(ActorModel.class))).thenReturn(getActors().get(0));

        actorService.patch(actor.getUuid(), mapper().valueToTree(actor));

        verify(actorRepository, times(1)).save(any(ActorModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateActor() {
        doThrow(ItemNotFoundException.class)
                .when(actorRepository).findById(any(UUID.class));

        JsonNode node = mapper().valueToTree(actor);

        assertThrows(ItemNotFoundException.class, () -> actorService.patch(actor.getUuid(), node));
        verify(actorRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteActor() {
        when(actorRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(actorRepository).deleteById(any(UUID.class));

        actorService.deleteById(actor.getUuid());

        verify(actorRepository, times(1)).existsById(any(UUID.class));
        verify(actorRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteActor() {
        when(actorRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(actorRepository).deleteById(any(UUID.class));

        assertThrows(ItemNotFoundException.class, () -> actorService.deleteById(actor.getUuid()));
        verify(actorRepository, times(1)).existsById(any(UUID.class));
        verify(actorRepository, times(1)).deleteById(any(UUID.class));
    }
}
