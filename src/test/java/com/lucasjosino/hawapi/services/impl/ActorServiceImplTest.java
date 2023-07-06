package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.models.ActorSocialModel;
import com.lucasjosino.hawapi.models.dto.ActorDTO;
import com.lucasjosino.hawapi.models.dto.ActorSocialDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import com.lucasjosino.hawapi.repositories.ActorSocialRepository;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ActorServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private ActorModel actorModel;

    private ActorDTO actorDTO;

    @InjectMocks
    private ActorServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private Random random;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ActorRepository repository;

    @Mock
    private ActorSocialRepository socialRepository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        actorDTO = new ActorDTO();
        actorDTO.setUuid(UUID.randomUUID());
        actorDTO.setHref("/api/v1/actors/" + actorDTO.getUuid());
        actorDTO.setFirstName("Lorem");
        actorDTO.setLastName("Ipsum");
        actorDTO.setNationality("American");
        actorDTO.setSeasons(Arrays.asList("/api/v1/seasons/1", "/api/v1/seasons/2"));
        actorDTO.setGender((byte) 1);
        actorDTO.setBirthDate(LocalDate.now());
        actorDTO.setCharacter("/api/v1/characters/1");
        actorDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        actorDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        actorDTO.setCreatedAt(LocalDateTime.now());
        actorDTO.setUpdatedAt(LocalDateTime.now());

        ActorSocialDTO socialDTO = new ActorSocialDTO();
        socialDTO.setHandle("@lorem");
        socialDTO.setUrl("https://twitter.com/lorem");
        socialDTO.setSocial("Twitter");

        actorDTO.setSocials(Collections.singleton(socialDTO));

        actorModel = mapper.map(actorDTO, ActorModel.class);
    }

    @Test
    void shouldReturnAllActorUUIDs() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());

        when(repository.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(repository.count()).thenReturn((long) uuids.size());

        Pageable pageable = Pageable.ofSize(1);
        Page<UUID> res = service.findAllUUIDs(pageable);

        assertFalse(res.isEmpty());
        assertEquals(uuids, res.getContent());
        assertEquals(pageable.getPageSize(), res.getTotalElements());
        verify(repository, times(1)).findAllUUIDs(any(Pageable.class));
        verify(repository, times(1)).count();
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllActorUUIDs() {
        List<UUID> uuids = Collections.emptyList();

        when(repository.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(repository.count()).thenReturn((long) 0);

        Pageable pageable = Pageable.ofSize(1);
        Page<UUID> res = service.findAllUUIDs(pageable);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAllUUIDs(any(Pageable.class));
        verify(repository, times(1)).count();
    }

    @Test
    void shouldReturnAllActors() {
        List<UUID> uuids = Collections.singletonList(actorModel.getUuid());
        List<ActorModel> data = Collections.singletonList(actorModel);
        ActorDTO[] returnData = {actorDTO};

        when(repository.findAll(Mockito.<Specification<ActorModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), any())).thenReturn(returnData);

        List<ActorDTO> res = service.findAll(new HashMap<>(), uuids);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(actorDTO, res.get(0));
        verify(repository, times(1)).findAll(Mockito.<Specification<ActorModel>>any());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllActors() {
        List<UUID> uuids = Collections.emptyList();
        List<ActorModel> data = Collections.emptyList();
        ActorDTO[] returnData = {};

        when(repository.findAll(Mockito.<Specification<ActorModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(ActorDTO[].class))).thenReturn(returnData);

        List<ActorDTO> res = service.findAll(new HashMap<>(), uuids);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll(Mockito.<Specification<ActorModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(ActorDTO[].class));
    }

    @Test
    void shouldReturnAllActorSocials() {
        List<ActorSocialModel> data = new ArrayList<>(actorModel.getSocials());
        List<ActorSocialDTO> dataDTO = new ArrayList<>(actorDTO.getSocials());
        ActorSocialDTO[] dataArray = dataDTO.toArray(new ActorSocialDTO[0]);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(socialRepository.findAll()).thenReturn(data);
        when(modelMapper.map(anyList(), eq(ActorSocialDTO[].class))).thenReturn(dataArray);

        List<ActorSocialDTO> res = service.findAllSocials(actorModel.getUuid());

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(dataDTO.get(0), res.get(0));
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(socialRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(anyList(), eq(ActorSocialDTO[].class));
    }

    @Test
    void whenNoSocialIsFoundShouldReturnEmptyListOnReturnAllActorSocial() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(socialRepository.findAll()).thenReturn(Collections.emptyList());
        when(modelMapper.map(any(), eq(ActorSocialDTO[].class))).thenReturn(new ActorSocialDTO[]{});

        List<ActorSocialDTO> res = service.findAllSocials(actorModel.getUuid());

        assertTrue(res.isEmpty());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(socialRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(), eq(ActorSocialDTO[].class));
    }

    @Test
    void whenNoActorIsFoundShouldThrowItemNotFoundExceptionOnReturnAllActorSocials() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.findAllSocials(actorModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldReturnRandomActor() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());
        List<ActorModel> data = Collections.singletonList(actorModel);
        Page<ActorModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);

        when(repository.count()).thenReturn((long) uuids.size());
        when(utils.getCountOrThrow(anyLong())).thenReturn((long) uuids.size());
        when(random.nextInt(anyInt())).thenReturn(1);
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), any())).thenReturn(actorDTO);

        ActorDTO res = service.findRandom("en-US");

        assertNotNull(res);
        assertEquals(actorDTO, res);
        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(repository, times(1)).findAll(any(PageRequest.class));
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnReturnRandomActor() {
        when(repository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandom("en-US"));

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnRandomActorSocial() {
        long count = 1;
        List<ActorSocialModel> data = new ArrayList<>(actorModel.getSocials());
        Page<ActorSocialModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);
        List<ActorSocialDTO> returnData = new ArrayList<>(actorDTO.getSocials());

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(socialRepository.count()).thenReturn(count);
        when(utils.getCountOrThrow(anyLong())).thenReturn(count);
        when(random.nextInt(anyInt())).thenReturn(1);
        when(socialRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(ActorSocialDTO.class))).thenReturn(returnData.get(0));

        ActorSocialDTO res = service.findRandomSocial(actorModel.getUuid());

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(socialRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(socialRepository, times(1)).findAll(any(PageRequest.class));
        verify(modelMapper, times(1)).map(any(), eq(ActorSocialDTO.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnReturnRandomActorSocial() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomSocial(actorModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void whenNoActorSocialFoundShouldThrowItemNotFoundExceptionOnReturnRandomActorSocial() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(socialRepository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomSocial(actorModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(socialRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnActorByUUID() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(actorModel));
        when(modelMapper.map(any(), any())).thenReturn(actorDTO);

        ActorDTO res = service.findBy(actorModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(actorDTO, res);
        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnReturnActorByUUID() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findBy(actorModel.getUuid(), "en-US"));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldReturnActorSocialById() {
        List<ActorSocialModel> data = new ArrayList<>(actorModel.getSocials());
        List<ActorSocialDTO> returnData = new ArrayList<>(actorDTO.getSocials());

        when(socialRepository.findByActorUuidAndSocial(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        when(modelMapper.map(any(), eq(ActorSocialDTO.class))).thenReturn(returnData.get(0));

        ActorSocialDTO res = service.findSocialBy(actorModel.getUuid(), "Twitter");

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(socialRepository, times(1)).findByActorUuidAndSocial(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(ActorSocialDTO.class));
    }

    @Test
    void whenNoActorSocialFoundShouldThrowItemNotFoundExceptionOnReturnActorSocialById() {
        when(socialRepository.findByActorUuidAndSocial(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findSocialBy(actorModel.getUuid(), "Twitter"));

        verify(socialRepository, times(1)).findByActorUuidAndSocial(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveActor() {
        when(modelMapper.map(any(), eq(ActorModel.class))).thenReturn(actorModel);
        when(repository.save(any(ActorModel.class))).thenReturn(actorModel);
        when(modelMapper.map(any(), eq(ActorDTO.class))).thenReturn(actorDTO);

        ActorDTO res = service.save(actorDTO);

        assertNotNull(res);
        assertEquals(actorDTO, res);
        verify(modelMapper, times(1)).map(any(), eq(ActorModel.class));
        verify(repository, times(1)).save(any(ActorModel.class));
        verify(modelMapper, times(1)).map(any(), eq(ActorDTO.class));
    }

    @Test
    void shouldSaveActorSocial() {
        List<ActorSocialModel> data = new ArrayList<>(actorModel.getSocials());
        List<ActorSocialDTO> returnData = new ArrayList<>(actorDTO.getSocials());

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(modelMapper.map(any(), eq(ActorSocialModel.class))).thenReturn(data.get(0));
        when(socialRepository.save(any(ActorSocialModel.class))).thenReturn(data.get(0));
        when(modelMapper.map(any(), eq(ActorSocialDTO.class))).thenReturn(returnData.get(0));

        ActorSocialDTO res = service.saveSocial(actorModel.getUuid(), returnData.get(0));

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(ActorSocialModel.class));
        verify(socialRepository, times(1)).save(any(ActorSocialModel.class));
        verify(modelMapper, times(1)).map(any(), eq(ActorSocialDTO.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnSaveActorSocial() {
        List<ActorSocialDTO> returnData = new ArrayList<>(actorDTO.getSocials());

        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.saveSocial(actorModel.getUuid(), returnData.get(0)));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldUpdateActor() throws IOException {
        ActorDTO patch = new ActorDTO();
        patch.setUuid(actorModel.getUuid());
        patch.setHref("/api/v1/" + actorModel.getUuid());
        actorModel.setHref("/api/v1/" + actorModel.getUuid());

        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(actorModel));
        when(modelMapper.map(any(), eq(ActorModel.class))).thenReturn(actorModel);
        when(utils.merge(any(ActorModel.class), any(ActorDTO.class))).thenReturn(actorModel);
        when(repository.save(any(ActorModel.class))).thenReturn(actorModel);

        service.patch(actorModel.getUuid(), patch);

        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
        verify(utils, times(1)).merge(any(ActorModel.class), any(ActorDTO.class));
        verify(repository, times(1)).save(any(ActorModel.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnUpdateActor() {
        ActorDTO patch = new ActorDTO();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patch(actorModel.getUuid(), patch));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldUpdateActorSocial() throws IOException {
        List<ActorSocialModel> data = new ArrayList<>(actorModel.getSocials());
        ActorSocialDTO patch = new ActorSocialDTO();
        patch.setSocial("Instagram");

        when(socialRepository.existsByActorUuidAndSocial(any(UUID.class), anyString())).thenReturn(true);
        when(socialRepository.findByActorUuidAndSocial(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        when(modelMapper.map(any(), eq(ActorSocialModel.class))).thenReturn(data.get(0));
        data.get(0).setSocial("Instagram");
        when(utils.merge(any(ActorSocialModel.class), any(ActorSocialDTO.class))).thenReturn(data.get(0));
        when(socialRepository.save(any(ActorSocialModel.class))).thenReturn(data.get(0));

        service.patchSocial(actorModel.getUuid(), "Twitter", patch);

        verify(socialRepository, times(1)).existsByActorUuidAndSocial(any(UUID.class), anyString());
        verify(socialRepository, times(1)).findByActorUuidAndSocial(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(ActorSocialModel.class));
        verify(utils, times(1)).merge(any(ActorSocialModel.class), any(ActorSocialDTO.class));
        verify(socialRepository, times(1)).save(any(ActorSocialModel.class));
    }

    @Test
    void whenNoActorSocialFoundShouldThrowItemNotFoundExceptionOnUpdateActorSocial() {
        ActorSocialDTO patch = new ActorSocialDTO();

        // TODO: Remove duplicate code
        when(socialRepository.existsByActorUuidAndSocial(any(UUID.class), anyString())).thenReturn(true);
        when(socialRepository.findByActorUuidAndSocial(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patchSocial(actorModel.getUuid(), "Twitter", patch));

        verify(socialRepository, times(1)).findByActorUuidAndSocial(any(UUID.class), anyString());
    }

    @Test
    void shouldDeleteActor() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.deleteById(actorModel.getUuid());

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoActorFoundShouldThrowItemNotFoundExceptionOnDeleteActor() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteById(actorModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldDeleteActorSocial() {
        when(socialRepository.existsByActorUuidAndSocial(any(UUID.class), anyString())).thenReturn(true);
        doNothing().when(socialRepository).deleteByActorUuidAndSocial(any(UUID.class), anyString());

        service.deleteSocial(actorModel.getUuid(), "Twitter");

        verify(socialRepository, times(1)).existsByActorUuidAndSocial(any(UUID.class), anyString());
        verify(socialRepository, times(1)).deleteByActorUuidAndSocial(any(UUID.class), anyString());
    }

    @Test
    void whenNoActorSocialFoundShouldThrowItemNotFoundExceptionOnDeleteActorSocial() {
        when(socialRepository.existsByActorUuidAndSocial(any(UUID.class), anyString())).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteSocial(actorModel.getUuid(), "Twitter"));

        verify(socialRepository, times(1)).existsByActorUuidAndSocial(any(UUID.class), anyString());
    }
}