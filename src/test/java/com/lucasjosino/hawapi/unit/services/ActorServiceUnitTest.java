package com.lucasjosino.hawapi.unit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.lucasjosino.hawapi.utils.TestsData.getActors;
import static com.lucasjosino.hawapi.utils.TestsData.getNewActor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class ActorServiceUnitTest {

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

        assertThat(res).hasFieldOrPropertyWithValue("uuid", newActor.getUuid());
        assertThat(res).hasFieldOrPropertyWithValue("href", newActor.getHref());
        assertThat(res).hasFieldOrPropertyWithValue("first_name", newActor.getFirstName());
        assertThat(res).hasFieldOrPropertyWithValue("last_name", newActor.getLastName());
        assertThat(res).hasFieldOrPropertyWithValue("gender", newActor.getGender());
        assertThat(res).hasFieldOrPropertyWithValue("character", newActor.getCharacter());
        verify(actorRepository, times(1)).save(any(ActorModel.class));
    }

    @Test
    public void shouldReturnActorByUUID() {
        ActorModel newActor = getNewActor();
        when(actorRepository.findById(any(UUID.class))).thenReturn(Optional.of(newActor));

        ActorModel res = actorService.findById(newActor.getUuid());

        assertEquals(newActor, res);
        verify(actorRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundActor() {
        ActorModel newActor = getNewActor();
        when(actorRepository.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        Exception exception = assertThrows(
                ItemNotFoundException.class,
                () -> actorService.findById(newActor.getUuid())
        );

        assertEquals(ItemNotFoundException.class, exception.getClass());
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
        List<ActorModel> filteredActorList = new ArrayList<>();
        filteredActorList.add(getActors().get(0));
        when(actorRepository.findAll(Mockito.<Example<ActorModel>>any())).thenReturn(filteredActorList);

        ActorFilter filter = Mockito.mock(ActorFilter.class);
        List<ActorModel> res = actorService.findAll(filter);

        assertEquals(1, res.size());
        verify(actorRepository, times(1)).findAll(Mockito.<Example<ActorModel>>any());
    }

    @Test
    public void shouldUpdateActor() throws JsonPatchException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        when(actorRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(getActors().get(0)));
        when(utils.mergePatch(any(), any(), any())).thenReturn(getActors().get(0));
        when(actorRepository.save(any(ActorModel.class))).thenReturn(getActors().get(0));

        ActorModel model = getActors().get(0);
        actorService.patch(model.getUuid(), mapper.valueToTree(model));

        verify(actorRepository, times(1)).save(any(ActorModel.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateActor() {
        ObjectMapper mapper = new ObjectMapper();

        doThrow(ItemNotFoundException.class)
                .when(actorRepository).findById(any(UUID.class));

        ActorModel model = getActors().get(0);
        Exception exception = assertThrows(
                ItemNotFoundException.class,
                () -> actorService.patch(model.getUuid(), mapper.valueToTree(model))
        );

        assertEquals(ItemNotFoundException.class, exception.getClass());
        verify(actorRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldDeleteActor() {
        when(actorRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(actorRepository).deleteById(any(UUID.class));

        ActorModel model = getActors().get(0);
        actorService.deleteById(model.getUuid());

        verify(actorRepository, times(1)).existsById(any(UUID.class));
        verify(actorRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteActor() {
        when(actorRepository.existsById(any(UUID.class))).thenReturn(true);
        doThrow(ItemNotFoundException.class)
                .when(actorRepository).deleteById(any(UUID.class));

        ActorModel model = getActors().get(0);
        Exception exception = assertThrows(
                ItemNotFoundException.class,
                () -> actorService.deleteById(model.getUuid())
        );

        assertEquals(ItemNotFoundException.class, exception.getClass());
        verify(actorRepository, times(1)).existsById(any(UUID.class));
        verify(actorRepository, times(1)).deleteById(any(UUID.class));
    }
}
