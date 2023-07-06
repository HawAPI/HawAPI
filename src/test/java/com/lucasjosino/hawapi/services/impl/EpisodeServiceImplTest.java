package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import com.lucasjosino.hawapi.models.dto.translation.EpisodeTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import com.lucasjosino.hawapi.repositories.translation.EpisodeTranslationRepository;
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
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EpisodeServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private EpisodeModel episodeModel;

    private EpisodeDTO episodeDTO;

    private EpisodeTranslation translation;

    private EpisodeTranslationDTO translationDTO;

    @InjectMocks
    private EpisodeServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private Random random;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EpisodeRepository repository;

    @Mock
    private EpisodeTranslationRepository translationRepository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        episodeDTO = new EpisodeDTO();
        episodeDTO.setUuid(UUID.randomUUID());
        episodeDTO.setHref("/api/v1/episodes/" + episodeDTO.getUuid());
        episodeDTO.setLanguages(Collections.singletonList("Lorem"));
        episodeDTO.setDuration(12482342);
        episodeDTO.setEpisodeNum((byte) 2);
        episodeDTO.setNextEpisode("/api/v1/episodes/3");
        episodeDTO.setPrevEpisode("/api/v1/episodes/1");
        episodeDTO.setSeason("/api/v1/seasons/1");
        episodeDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        episodeDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        episodeDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        episodeDTO.setCreatedAt(LocalDateTime.now());
        episodeDTO.setUpdatedAt(LocalDateTime.now());
        episodeDTO.setLanguage("en-US");
        episodeDTO.setTitle("Lorem Ipsum");
        episodeDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translationDTO = new EpisodeTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setTitle("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        episodeModel = mapper.map(episodeDTO, EpisodeModel.class);
        translation = mapper.map(translationDTO, EpisodeTranslation.class);
        translation.setEpisodeUuid(episodeModel.getUuid());

        episodeModel.setTranslation(translation);
    }

    @Test
    void shouldReturnAllEpisodeUUIDs() {
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
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllEpisodeUUIDs() {
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
    void shouldReturnAllEpisodes() {
        List<UUID> uuids = Collections.singletonList(episodeModel.getUuid());
        List<EpisodeModel> data = Collections.singletonList(episodeModel);
        EpisodeDTO[] returnData = {episodeDTO};

        when(repository.findAll(Mockito.<Specification<EpisodeModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), any())).thenReturn(returnData);

        List<EpisodeDTO> res = service.findAll(new HashMap<>(), uuids);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(episodeDTO, res.get(0));
        verify(repository, times(1)).findAll(Mockito.<Specification<EpisodeModel>>any());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllEpisodes() {
        List<UUID> uuids = Collections.emptyList();
        List<EpisodeModel> data = Collections.emptyList();
        EpisodeDTO[] returnData = {};

        when(repository.findAll(Mockito.<Specification<EpisodeModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(EpisodeDTO[].class))).thenReturn(returnData);

        List<EpisodeDTO> res = service.findAll(new HashMap<>(), uuids);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll(Mockito.<Specification<EpisodeModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(EpisodeDTO[].class));
    }

    @Test
    void shouldReturnAllEpisodeTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllByEpisodeUuid(any(UUID.class)))
                .thenReturn(Collections.singletonList(translation));
        when(modelMapper.map(anyList(), eq(EpisodeTranslationDTO[].class)))
                .thenReturn(new EpisodeTranslationDTO[]{translationDTO});

        List<EpisodeTranslationDTO> res = service.findAllTranslationsBy(episodeModel.getUuid());

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(translationDTO, res.get(0));
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllByEpisodeUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(anyList(), eq(EpisodeTranslationDTO[].class));
    }

    @Test
    void whenNoTranslationIsFoundShouldReturnEmptyListOnReturnAllEpisodeTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllByEpisodeUuid(any(UUID.class))).thenReturn(Collections.emptyList());
        when(modelMapper.map(any(), eq(EpisodeTranslationDTO[].class))).thenReturn(new EpisodeTranslationDTO[]{});

        List<EpisodeTranslationDTO> res = service.findAllTranslationsBy(episodeModel.getUuid());

        assertTrue(res.isEmpty());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllByEpisodeUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(EpisodeTranslationDTO[].class));
    }

    @Test
    void whenNoEpisodeIsFoundShouldThrowItemNotFoundExceptionOnReturnAllEpisodeTranslations() {
        when(translationRepository.findByEpisodeUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(episodeModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).findByEpisodeUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnRandomEpisode() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());
        List<EpisodeModel> data = Collections.singletonList(episodeModel);
        Page<EpisodeModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);

        when(repository.count()).thenReturn((long) uuids.size());
        when(utils.getCountOrThrow(anyLong())).thenReturn((long) uuids.size());
        when(random.nextInt(anyInt())).thenReturn(1);
        when(repository.findAll(Mockito.<Specification<EpisodeModel>>any(), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), any())).thenReturn(episodeDTO);

        EpisodeDTO res = service.findRandom("en-US");

        assertNotNull(res);
        assertEquals(episodeDTO, res);
        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(repository, times(1)).findAll(
                Mockito.<Specification<EpisodeModel>>any(),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnReturnRandomEpisode() {
        when(repository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandom("en-US"));

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnRandomEpisodeTranslation() {
        long count = 1;
        List<EpisodeTranslation> data = Collections.singletonList(translation);
        Page<EpisodeTranslation> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);
        List<EpisodeTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn(count);
        when(utils.getCountOrThrow(anyLong())).thenReturn(count);
        when(random.nextInt(anyInt())).thenReturn(1);
        when(translationRepository.findAllByEpisodeUuid(any(UUID.class), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(EpisodeTranslationDTO.class))).thenReturn(returnData.get(0));

        EpisodeTranslationDTO res = service.findRandomTranslation(episodeModel.getUuid());

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(translationRepository, times(1)).findAllByEpisodeUuid(
                any(UUID.class),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), eq(EpisodeTranslationDTO.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnReturnRandomEpisodeTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(episodeModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void whenNoEpisodeTranslationFoundShouldThrowItemNotFoundExceptionOnReturnRandomEpisodeTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(episodeModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnEpisodeByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.ofNullable(episodeModel));
        when(modelMapper.map(any(), any())).thenReturn(episodeDTO);

        EpisodeDTO res = service.findBy(episodeModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(episodeDTO, res);
        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnReturnEpisodeByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findBy(episodeModel.getUuid(), "en-US"));

        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnEpisodeTranslationById() {
        List<EpisodeTranslation> data = Collections.singletonList(translation);
        List<EpisodeTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(translationRepository.findByEpisodeUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        when(modelMapper.map(any(), eq(EpisodeTranslationDTO.class))).thenReturn(returnData.get(0));

        EpisodeTranslationDTO res = service.findTranslationBy(episodeModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(translationRepository, times(1))
                .findByEpisodeUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(EpisodeTranslationDTO.class));
    }

    @Test
    void whenNoEpisodeTranslationFoundShouldThrowItemNotFoundExceptionOnReturnEpisodeTranslationById() {
        when(translationRepository.findByEpisodeUuidAndLanguage(any(UUID.class),
                anyString()
        )).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(episodeModel.getUuid(), "en-US"));

        verify(translationRepository, times(1))
                .findByEpisodeUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveEpisode() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.existsByEpisodeUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);
        when(modelMapper.map(any(), eq(EpisodeModel.class))).thenReturn(episodeModel);
        when(repository.save(any(EpisodeModel.class))).thenReturn(episodeModel);
        when(modelMapper.map(any(), eq(EpisodeDTO.class))).thenReturn(episodeDTO);

        EpisodeDTO res = service.save(episodeDTO);

        assertNotNull(res);
        assertEquals(episodeDTO, res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1))
                .existsByEpisodeUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(EpisodeModel.class));
        verify(repository, times(1)).save(any(EpisodeModel.class));
        verify(modelMapper, times(1)).map(any(), eq(EpisodeDTO.class));
    }

    @Test
    void whenInvalidLanguageIsProvidedShouldThrowBadRequestExceptionOnSaveEpisode() {
        EpisodeDTO newEpisode = new EpisodeDTO();
        newEpisode.setLanguage("");

        assertThrows(BadRequestException.class, () -> service.save(newEpisode));
    }

    @Test
    void whenTranslationAlreadyFoundShouldThrowSaveConflictExceptionOnSaveEpisode() {
        EpisodeDTO newEpisode = new EpisodeDTO();
        newEpisode.setLanguage("en-US");

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.existsByEpisodeUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);

        assertThrows(SaveConflictException.class, () -> service.save(newEpisode));

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).existsByEpisodeUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveEpisodeTranslation() {
        List<EpisodeTranslation> data = Collections.singletonList(translation);
        List<EpisodeTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(modelMapper.map(any(), eq(EpisodeTranslation.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(EpisodeTranslation.class))).thenReturn(data.get(0));
        when(modelMapper.map(any(), eq(EpisodeTranslationDTO.class))).thenReturn(returnData.get(0));

        EpisodeTranslationDTO res = service.saveTranslation(episodeModel.getUuid(), returnData.get(0));

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(EpisodeTranslation.class));
        verify(translationRepository, times(1)).save(any(EpisodeTranslation.class));
        verify(modelMapper, times(1)).map(any(), eq(EpisodeTranslationDTO.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnSaveEpisodeTranslation() {
        List<EpisodeTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> service.saveTranslation(episodeModel.getUuid(), returnData.get(0))
        );

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldUpdateEpisode() throws IOException {
        EpisodeDTO patch = new EpisodeDTO();
        patch.setUuid(episodeModel.getUuid());
        patch.setHref("/api/v1/" + episodeModel.getUuid());
        episodeModel.setHref("/api/v1/" + episodeModel.getUuid());

        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(episodeModel));
        when(modelMapper.map(any(), eq(EpisodeModel.class))).thenReturn(episodeModel);
        when(utils.merge(any(EpisodeModel.class), any(EpisodeDTO.class))).thenReturn(episodeModel);
        when(repository.save(any(EpisodeModel.class))).thenReturn(episodeModel);

        service.patch(episodeModel.getUuid(), patch);

        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
        verify(utils, times(1)).merge(any(EpisodeModel.class), any(EpisodeDTO.class));
        verify(repository, times(1)).save(any(EpisodeModel.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnUpdateEpisode() {
        EpisodeDTO patch = new EpisodeDTO();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patch(episodeModel.getUuid(), patch));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldUpdateEpisodeTranslation() throws IOException {
        List<EpisodeTranslation> data = Collections.singletonList(translation);
        EpisodeTranslationDTO patch = new EpisodeTranslationDTO();
        patch.setTitle("Lorem Ipsum");

        when(translationRepository.findByEpisodeUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        data.get(0).setTitle("Lorem Ipsum");
        when(utils.merge(any(EpisodeTranslation.class), any(EpisodeTranslationDTO.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(EpisodeTranslation.class))).thenReturn(data.get(0));

        service.patchTranslation(episodeModel.getUuid(), "en-US", patch);

        verify(translationRepository, times(1)).findByEpisodeUuidAndLanguage(any(UUID.class), anyString());
        verify(utils, times(1)).merge(any(EpisodeTranslation.class), any(EpisodeTranslationDTO.class));
        verify(translationRepository, times(1)).save(any(EpisodeTranslation.class));
    }

    @Test
    void whenNoEpisodeTranslationFoundShouldThrowItemNotFoundExceptionOnUpdateEpisodeTranslation() {
        EpisodeTranslationDTO patch = new EpisodeTranslationDTO();

        // TODO: Remove duplicate code
        when(translationRepository.findByEpisodeUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.patchTranslation(episodeModel.getUuid(), "en-US", patch)
        );

        verify(translationRepository, times(1)).findByEpisodeUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldDeleteEpisode() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.deleteById(episodeModel.getUuid());

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoEpisodeFoundShouldThrowItemNotFoundExceptionOnDeleteEpisode() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteById(episodeModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldDeleteEpisodeTranslation() {
        when(translationRepository.existsByEpisodeUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);
        doNothing().when(translationRepository).deleteByEpisodeUuidAndLanguage(any(UUID.class), anyString());

        service.deleteTranslation(episodeModel.getUuid(), "en-US");

        verify(translationRepository, times(1)).existsByEpisodeUuidAndLanguage(any(UUID.class), anyString());
        verify(translationRepository, times(1)).deleteByEpisodeUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void whenNoEpisodeTranslationFoundShouldThrowItemNotFoundExceptionOnDeleteEpisodeTranslation() {
        when(translationRepository.existsByEpisodeUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteTranslation(episodeModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).existsByEpisodeUuidAndLanguage(any(UUID.class), anyString());
    }
}