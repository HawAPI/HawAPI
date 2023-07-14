package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CharacterServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private CharacterModel characterModel;

    private CharacterDTO characterDTO;

    @InjectMocks
    private CharacterServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private Random random;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CharacterRepository repository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        characterDTO = new CharacterDTO();
        characterDTO.setUuid(UUID.randomUUID());
        characterDTO.setHref("/api/v1/characters/" + characterDTO.getUuid());
        characterDTO.setFirstName("Lorem");
        characterDTO.setLastName("Ipsum");
        characterDTO.setNicknames(Arrays.asList("lore", "locum"));
        characterDTO.setBirthDate(LocalDate.now());
        characterDTO.setDeathDate(LocalDate.now());
        characterDTO.setGender((byte) 1);
        characterDTO.setActor("/api/v1/actors/1");
        characterDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        characterDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        characterDTO.setCreatedAt(LocalDateTime.now());
        characterDTO.setUpdatedAt(LocalDateTime.now());

        characterModel = mapper.map(characterDTO, CharacterModel.class);
    }

    @Test
    void shouldReturnAllCharacterUUIDs() {
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
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllCharacterUUIDs() {
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
    void shouldReturnAllCharacters() {
        List<UUID> uuids = Collections.singletonList(characterModel.getUuid());
        List<CharacterModel> data = Collections.singletonList(characterModel);
        CharacterDTO[] returnData = {characterDTO};

        when(repository.findAll(Mockito.<Specification<CharacterModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(CharacterDTO[].class))).thenReturn(returnData);

        List<CharacterDTO> res = service.findAll(new HashMap<>(), uuids);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(characterDTO, res.get(0));
        verify(repository, times(1)).findAll(Mockito.<Specification<CharacterModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(CharacterDTO[].class));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllCharacters() {
        List<UUID> uuids = Collections.emptyList();
        List<CharacterModel> data = Collections.emptyList();
        CharacterDTO[] returnData = {};

        when(repository.findAll(Mockito.<Specification<CharacterModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(CharacterDTO[].class))).thenReturn(returnData);

        List<CharacterDTO> res = service.findAll(new HashMap<>(), uuids);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll(Mockito.<Specification<CharacterModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(CharacterDTO[].class));
    }

    @Test
    void shouldReturnRandomCharacter() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());
        List<CharacterModel> data = Collections.singletonList(characterModel);
        Page<CharacterModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);

        when(repository.count()).thenReturn((long) uuids.size());
        when(utils.getCountOrThrow(anyLong())).thenReturn((long) uuids.size());
        when(random.nextInt(anyInt())).thenReturn(1);
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(CharacterDTO.class))).thenReturn(characterDTO);

        CharacterDTO res = service.findRandom("en-US");

        assertNotNull(res);
        assertEquals(characterDTO, res);
        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(repository, times(1)).findAll(any(PageRequest.class));
        verify(modelMapper, times(1)).map(any(), eq(CharacterDTO.class));
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnReturnRandomCharacter() {
        when(repository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandom("en-US"));

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnCharacterByUUID() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(characterModel));
        when(modelMapper.map(any(), any())).thenReturn(characterDTO);

        CharacterDTO res = service.findBy(characterModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(characterDTO, res);
        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnReturnCharacterByUUID() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findBy(characterModel.getUuid(), "en-US"));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldSaveCharacter() {
        when(modelMapper.map(any(), eq(CharacterModel.class))).thenReturn(characterModel);
        when(repository.save(any(CharacterModel.class))).thenReturn(characterModel);
        when(modelMapper.map(any(), eq(CharacterDTO.class))).thenReturn(characterDTO);

        CharacterDTO res = service.save(characterDTO);

        assertNotNull(res);
        assertEquals(characterDTO, res);
        verify(modelMapper, times(1)).map(any(), eq(CharacterModel.class));
        verify(repository, times(1)).save(any(CharacterModel.class));
        verify(modelMapper, times(1)).map(any(), eq(CharacterDTO.class));
    }

    @Test
    void shouldUpdateCharacter() throws IOException {
        CharacterDTO patch = new CharacterDTO();
        patch.setUuid(characterModel.getUuid());
        patch.setHref("/api/v1/" + characterModel.getUuid());
        characterModel.setHref("/api/v1/" + characterModel.getUuid());

        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(characterModel));
        when(modelMapper.map(any(), eq(CharacterModel.class))).thenReturn(characterModel);
        when(utils.merge(any(CharacterModel.class), any(CharacterDTO.class))).thenReturn(characterModel);
        when(repository.save(any(CharacterModel.class))).thenReturn(characterModel);

        service.patch(characterModel.getUuid(), patch);

        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
        verify(utils, times(1)).merge(any(CharacterModel.class), any(CharacterDTO.class));
        verify(repository, times(1)).save(any(CharacterModel.class));
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnUpdateCharacter() {
        CharacterDTO patch = new CharacterDTO();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patch(characterModel.getUuid(), patch));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldDeleteCharacter() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.deleteById(characterModel.getUuid());

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoCharacterFoundShouldThrowItemNotFoundExceptionOnDeleteCharacter() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteById(characterModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }
}
