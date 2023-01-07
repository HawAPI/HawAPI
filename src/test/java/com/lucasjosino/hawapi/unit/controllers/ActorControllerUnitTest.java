package com.lucasjosino.hawapi.unit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.UnitTestConfig;
import com.lucasjosino.hawapi.controllers.ActorController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.services.ActorService;
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

import static com.lucasjosino.hawapi.utils.ModelAssertions.assertActorEquals;
import static com.lucasjosino.hawapi.utils.TestsData.getActors;
import static com.lucasjosino.hawapi.utils.TestsData.getNewActor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTestConfig
public class ActorControllerUnitTest {

    @Mock
    private ActorService actorService;

    @InjectMocks
    private ActorController actorController;

    @Test
    public void shouldCreateActor() {
        ActorModel newActor = getNewActor();
        when(actorService.save(any(ActorModel.class))).thenReturn(newActor);

        ResponseEntity<ActorModel> res = actorController.save(newActor);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertActorEquals(newActor, res);
        verify(actorService, times(1)).save(any(ActorModel.class));
    }

    @Test
    public void shouldReturnActorByUUID() {
        ActorModel newActor = getNewActor();
        when(actorService.findById(any(UUID.class))).thenReturn(newActor);

        ResponseEntity<ActorModel> res = actorController.findByUUID(newActor.getUuid());

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertActorEquals(newActor, res);
        verify(actorService, times(1)).findById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundActor() {
        ActorModel newActor = getNewActor();
        when(actorService.findById(any(UUID.class))).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> actorController.findByUUID(newActor.getUuid()));
        verify(actorService, times(1)).findById(any(UUID.class));
    }


    @Test
    public void shouldReturnListOfActors() {
        when(actorService.findAll(null)).thenReturn(getActors());

        ResponseEntity<List<ActorModel>> res = actorController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(2, res.getBody().size());
        verify(actorService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnEmptyListOfActors() {
        when(actorService.findAll(null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<ActorModel>> res = actorController.findAll(null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(Collections.EMPTY_LIST, res.getBody());
        verify(actorService, times(1)).findAll(null);
    }

    @Test
    public void shouldReturnListOfActorsWithFilter() {
        List<ActorModel> filteredActorList = new ArrayList<>();
        filteredActorList.add(getActors().get(0));
        when(actorService.findAll(any(ActorFilter.class))).thenReturn(filteredActorList);

        ActorFilter filter = Mockito.mock(ActorFilter.class);
        ResponseEntity<List<ActorModel>> res = actorController.findAll(filter);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertThat(res.getBody()).isNotNull();
        assertEquals(1, res.getBody().size());
        verify(actorService, times(1)).findAll(any(ActorFilter.class));
    }

    @Test
    public void shouldUpdateActor() throws JsonPatchException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        doNothing()
                .when(actorService).patch(any(UUID.class), any(JsonNode.class));

        ActorModel model = getActors().get(0);
        ResponseEntity<Void> res = actorController.patch(model.getUuid(), mapper.valueToTree(model));

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(actorService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldReturnNotFoundUpdateActor() throws JsonPatchException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        doThrow(ItemNotFoundException.class)
                .when(actorService).patch(any(UUID.class), any(JsonNode.class));

        ActorModel model = getActors().get(0);
        JsonNode node = mapper.valueToTree(model);

        assertThrows(ItemNotFoundException.class, () -> actorController.patch(model.getUuid(), node));
        verify(actorService, times(1)).patch(any(UUID.class), any(JsonNode.class));
    }

    @Test
    public void shouldDeleteActor() {
        doNothing()
                .when(actorService).deleteById(any(UUID.class));

        ActorModel model = getActors().get(0);
        ResponseEntity<Void> res = actorController.delete(model.getUuid());

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(actorService, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void shouldReturnNotFoundDeleteActor() {
        doThrow(ItemNotFoundException.class)
                .when(actorService).deleteById(any(UUID.class));

        ActorModel model = getActors().get(0);

        assertThrows(ItemNotFoundException.class, () -> actorController.delete(model.getUuid()));
        verify(actorService, times(1)).deleteById(any(UUID.class));
    }
}
