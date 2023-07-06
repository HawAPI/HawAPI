package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
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
public class SoundtrackServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private SoundtrackModel soundtrackModel;

    private SoundtrackDTO soundtrackDTO;

    @InjectMocks
    private SoundtrackServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private Random random;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SoundtrackRepository repository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        soundtrackDTO = new SoundtrackDTO();
        soundtrackDTO.setUuid(UUID.randomUUID());
        soundtrackDTO.setHref("/api/v1/soundtracks/" + soundtrackDTO.getUuid());
        soundtrackDTO.setName("Lorem");
        soundtrackDTO.setArtist("Ipsum");
        soundtrackDTO.setAlbum("Lorem Ipsum");
        soundtrackDTO.setUrls(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));
        soundtrackDTO.setDuration(158351809);
        soundtrackDTO.setReleaseDate(LocalDate.now());
        soundtrackDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        soundtrackDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        soundtrackDTO.setCreatedAt(LocalDateTime.now());
        soundtrackDTO.setUpdatedAt(LocalDateTime.now());

        soundtrackModel = mapper.map(soundtrackDTO, SoundtrackModel.class);
    }

    @Test
    void shouldReturnAllSoundtrackUUIDs() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());

        when(repository.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(repository.count()).thenReturn((long) uuids.size());

        Pageable pageable = Pageable.ofSize(1);
        Page<UUID> res = service.findAllUUIDs(pageable);

        assertEquals(uuids, res.getContent());
        assertEquals(pageable.getPageSize(), res.getTotalElements());
        verify(repository, times(1)).findAllUUIDs(any(Pageable.class));
        verify(repository, times(1)).count();
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllSoundtrackUUIDs() {
        List<UUID> uuids = Collections.emptyList();

        when(repository.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);
        when(repository.count()).thenReturn((long) 0);

        Pageable pageable = Pageable.ofSize(1);
        Page<UUID> res = service.findAllUUIDs(pageable);

        assertTrue(res.isEmpty());
        assertEquals(uuids, res.getContent());
        verify(repository, times(1)).findAllUUIDs(any(Pageable.class));
        verify(repository, times(1)).count();
    }

    @Test
    void shouldReturnAllSoundtracks() {
        List<UUID> uuids = Collections.singletonList(soundtrackModel.getUuid());
        List<SoundtrackModel> data = Collections.singletonList(soundtrackModel);
        SoundtrackDTO[] returnData = {soundtrackDTO};

        when(repository.findAll(Mockito.<Specification<SoundtrackModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(SoundtrackDTO[].class))).thenReturn(returnData);

        List<SoundtrackDTO> res = service.findAll(new HashMap<>(), uuids);

        verify(repository, times(1)).findAll(Mockito.<Specification<SoundtrackModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(SoundtrackDTO[].class));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllSoundtracks() {
        List<UUID> uuids = Collections.emptyList();
        List<SoundtrackModel> data = Collections.emptyList();
        SoundtrackDTO[] returnData = {};

        when(repository.findAll(Mockito.<Specification<SoundtrackModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(SoundtrackDTO[].class))).thenReturn(returnData);

        List<SoundtrackDTO> res = service.findAll(new HashMap<>(), uuids);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll(Mockito.<Specification<SoundtrackModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(SoundtrackDTO[].class));
    }

    @Test
    void shouldReturnRandomSoundtrack() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());
        List<SoundtrackModel> data = Collections.singletonList(soundtrackModel);
        Page<SoundtrackModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);

        when(repository.count()).thenReturn((long) uuids.size());
        when(utils.getCountOrThrow(anyLong())).thenReturn((long) uuids.size());
        when(random.nextInt(anyInt())).thenReturn(1);
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(SoundtrackDTO.class))).thenReturn(soundtrackDTO);

        SoundtrackDTO res = service.findRandom("en-US");

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(repository, times(1)).findAll(any(PageRequest.class));
        verify(modelMapper, times(1)).map(any(), eq(SoundtrackDTO.class));
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnReturnRandomSoundtrack() {
        when(repository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandom("en-US"));

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnSoundtrackByUUID() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(soundtrackModel));
        when(modelMapper.map(any(), any())).thenReturn(soundtrackDTO);

        SoundtrackDTO res = service.findBy(soundtrackModel.getUuid(), "en-US");

        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnReturnSoundtrackByUUID() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findBy(soundtrackModel.getUuid(), "en-US"));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldSaveSoundtrack() {
        when(modelMapper.map(any(), eq(SoundtrackModel.class))).thenReturn(soundtrackModel);
        when(repository.save(any(SoundtrackModel.class))).thenReturn(soundtrackModel);
        when(modelMapper.map(any(), eq(SoundtrackDTO.class))).thenReturn(soundtrackDTO);

        SoundtrackDTO res = service.save(soundtrackDTO);

        verify(modelMapper, times(1)).map(any(), eq(SoundtrackModel.class));
        verify(repository, times(1)).save(any(SoundtrackModel.class));
        verify(modelMapper, times(1)).map(any(), eq(SoundtrackDTO.class));
    }

    @Test
    void shouldUpdateSoundtrack() throws IOException {
        SoundtrackDTO patch = new SoundtrackDTO();
        patch.setUuid(soundtrackModel.getUuid());
        patch.setHref("/api/v1/" + soundtrackModel.getUuid());
        soundtrackModel.setHref("/api/v1/" + soundtrackModel.getUuid());

        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(soundtrackModel));
        when(modelMapper.map(any(), eq(SoundtrackModel.class))).thenReturn(soundtrackModel);
        when(utils.merge(any(SoundtrackModel.class), any(SoundtrackDTO.class))).thenReturn(soundtrackModel);
        when(repository.save(any(SoundtrackModel.class))).thenReturn(soundtrackModel);

        service.patch(soundtrackModel.getUuid(), patch);

        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
        verify(utils, times(1)).merge(any(SoundtrackModel.class), any(SoundtrackDTO.class));
        verify(repository, times(1)).save(any(SoundtrackModel.class));
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnUpdateSoundtrack() {
        SoundtrackDTO patch = new SoundtrackDTO();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patch(soundtrackModel.getUuid(), patch));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldDeleteSoundtrack() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.deleteById(soundtrackModel.getUuid());

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoSoundtrackFoundShouldThrowItemNotFoundExceptionOnDeleteSoundtrack() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteById(soundtrackModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }
}
