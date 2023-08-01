package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.models.dto.LocationDTO;
import com.lucasjosino.hawapi.models.dto.translation.LocationTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import com.lucasjosino.hawapi.repositories.translation.LocationTranslationRepository;
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
class LocationServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private LocationModel locationModel;

    private LocationDTO locationDTO;

    private LocationTranslation translation;

    private LocationTranslationDTO translationDTO;

    @InjectMocks
    private LocationServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private Random random;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private LocationRepository repository;

    @Mock
    private LocationTranslationRepository translationRepository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        locationDTO = new LocationDTO();
        locationDTO.setUuid(UUID.randomUUID());
        locationDTO.setHref("/api/v1/locations/" + locationDTO.getUuid());
        locationDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        locationDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        locationDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        locationDTO.setCreatedAt(LocalDateTime.now());
        locationDTO.setUpdatedAt(LocalDateTime.now());
        locationDTO.setLanguage("en-US");
        locationDTO.setName("Lorem Ipsum");
        locationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translationDTO = new LocationTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setName("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        locationModel = mapper.map(locationDTO, LocationModel.class);
        translation = mapper.map(translationDTO, LocationTranslation.class);
        translation.setLocationUuid(locationModel.getUuid());

        locationModel.setTranslation(translation);
    }

    @Test
    void shouldReturnAllLocationUUIDs() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());

        when(repository.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);

        Pageable pageable = Pageable.ofSize(1);
        Page<UUID> res = service.findAllUUIDs(pageable, uuids.size());

        assertFalse(res.isEmpty());
        assertEquals(uuids, res.getContent());
        assertEquals(pageable.getPageSize(), res.getTotalElements());
        verify(repository, times(1)).findAllUUIDs(any(Pageable.class));
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllLocationUUIDs() {
        List<UUID> uuids = Collections.emptyList();

        when(repository.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);

        Pageable pageable = Pageable.ofSize(1);
        Page<UUID> res = service.findAllUUIDs(pageable, 0);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAllUUIDs(any(Pageable.class));
    }

    @Test
    void shouldReturnAllLocations() {
        List<UUID> uuids = Collections.singletonList(locationModel.getUuid());
        List<LocationModel> data = Collections.singletonList(locationModel);
        LocationDTO[] returnData = {locationDTO};

        when(repository.findAll(Mockito.<Specification<LocationModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), any())).thenReturn(returnData);

        List<LocationDTO> res = service.findAll(new HashMap<>(), uuids);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(locationDTO, res.get(0));
        verify(repository, times(1)).findAll(Mockito.<Specification<LocationModel>>any());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllLocations() {
        List<UUID> uuids = Collections.emptyList();
        List<LocationModel> data = Collections.emptyList();
        LocationDTO[] returnData = {};

        when(repository.findAll(Mockito.<Specification<LocationModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(LocationDTO[].class))).thenReturn(returnData);

        List<LocationDTO> res = service.findAll(new HashMap<>(), uuids);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll(Mockito.<Specification<LocationModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(LocationDTO[].class));
    }

    @Test
    void shouldReturnAllLocationTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllByLocationUuid(any(UUID.class)))
                .thenReturn(Collections.singletonList(translation));
        when(modelMapper.map(anyList(), eq(LocationTranslationDTO[].class)))
                .thenReturn(new LocationTranslationDTO[]{translationDTO});

        List<LocationTranslationDTO> res = service.findAllTranslationsBy(locationModel.getUuid());

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(translationDTO, res.get(0));
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllByLocationUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(anyList(), eq(LocationTranslationDTO[].class));
    }

    @Test
    void whenNoTranslationIsFoundShouldReturnEmptyListOnReturnAllLocationTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllByLocationUuid(any(UUID.class))).thenReturn(Collections.emptyList());
        when(modelMapper.map(any(), eq(LocationTranslationDTO[].class))).thenReturn(new LocationTranslationDTO[]{});

        List<LocationTranslationDTO> res = service.findAllTranslationsBy(locationModel.getUuid());

        assertTrue(res.isEmpty());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllByLocationUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(LocationTranslationDTO[].class));
    }

    @Test
    void whenNoLocationIsFoundShouldThrowItemNotFoundExceptionOnReturnAllLocationTranslations() {
        when(translationRepository.findByLocationUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(locationModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).findByLocationUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnRandomLocation() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());
        List<LocationModel> data = Collections.singletonList(locationModel);
        Page<LocationModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);

        when(repository.count()).thenReturn((long) uuids.size());
        when(utils.getCountOrThrow(anyLong())).thenReturn((long) uuids.size());
        when(random.nextInt(anyInt())).thenReturn(1);
        when(repository.findAll(Mockito.<Specification<LocationModel>>any(), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), any())).thenReturn(locationDTO);

        LocationDTO res = service.findRandom("en-US");

        assertNotNull(res);
        assertEquals(locationDTO, res);
        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(repository, times(1)).findAll(
                Mockito.<Specification<LocationModel>>any(),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnReturnRandomLocation() {
        when(repository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandom("en-US"));

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnRandomLocationTranslation() {
        long count = 1;
        List<LocationTranslation> data = Collections.singletonList(translation);
        Page<LocationTranslation> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);
        List<LocationTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn(count);
        when(utils.getCountOrThrow(anyLong())).thenReturn(count);
        when(random.nextInt(anyInt())).thenReturn(1);
        when(translationRepository.findAllByLocationUuid(any(UUID.class), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(LocationTranslationDTO.class))).thenReturn(returnData.get(0));

        LocationTranslationDTO res = service.findRandomTranslation(locationModel.getUuid());

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(translationRepository, times(1)).findAllByLocationUuid(
                any(UUID.class),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), eq(LocationTranslationDTO.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnReturnRandomLocationTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(locationModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void whenNoLocationTranslationFoundShouldThrowItemNotFoundExceptionOnReturnRandomLocationTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(locationModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnLocationByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.ofNullable(locationModel));
        when(modelMapper.map(any(), any())).thenReturn(locationDTO);

        LocationDTO res = service.findBy(locationModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(locationDTO, res);
        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnReturnLocationByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findBy(locationModel.getUuid(), "en-US"));

        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnLocationTranslationById() {
        List<LocationTranslation> data = Collections.singletonList(translation);
        List<LocationTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(translationRepository.findByLocationUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        when(modelMapper.map(any(), eq(LocationTranslationDTO.class))).thenReturn(returnData.get(0));

        LocationTranslationDTO res = service.findTranslationBy(locationModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(translationRepository, times(1))
                .findByLocationUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(LocationTranslationDTO.class));
    }

    @Test
    void whenNoLocationTranslationFoundShouldThrowItemNotFoundExceptionOnReturnLocationTranslationById() {
        when(translationRepository.findByLocationUuidAndLanguage(any(UUID.class),
                anyString()
        )).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(locationModel.getUuid(), "en-US"));

        verify(translationRepository, times(1))
                .findByLocationUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveLocation() {
        when(translationRepository.existsByLocationUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);
        when(modelMapper.map(any(), eq(LocationModel.class))).thenReturn(locationModel);
        when(repository.save(any(LocationModel.class))).thenReturn(locationModel);
        when(modelMapper.map(any(), eq(LocationDTO.class))).thenReturn(locationDTO);

        LocationDTO res = service.save(locationDTO);

        assertNotNull(res);
        assertEquals(locationDTO, res);
        verify(translationRepository, times(1))
                .existsByLocationUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(LocationModel.class));
        verify(repository, times(1)).save(any(LocationModel.class));
        verify(modelMapper, times(1)).map(any(), eq(LocationDTO.class));
    }

    @Test
    void whenInvalidLanguageIsProvidedShouldThrowBadRequestExceptionOnSaveLocation() {
        LocationDTO newLocation = new LocationDTO();
        newLocation.setLanguage("");

        assertThrows(BadRequestException.class, () -> service.save(newLocation));
    }

    @Test
    void whenTranslationAlreadyExistsShouldThrowSaveConflictExceptionOnSaveLocation() {
        LocationDTO newLocation = new LocationDTO();
        newLocation.setLanguage("en-US");

        when(translationRepository.existsByLocationUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);

        assertThrows(SaveConflictException.class, () -> service.save(newLocation));

        verify(translationRepository, times(1)).existsByLocationUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveLocationTranslation() {
        List<LocationTranslation> data = Collections.singletonList(translation);
        List<LocationTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(modelMapper.map(any(), eq(LocationTranslation.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(LocationTranslation.class))).thenReturn(data.get(0));
        when(modelMapper.map(any(), eq(LocationTranslationDTO.class))).thenReturn(returnData.get(0));

        LocationTranslationDTO res = service.saveTranslation(locationModel.getUuid(), returnData.get(0));

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(LocationTranslation.class));
        verify(translationRepository, times(1)).save(any(LocationTranslation.class));
        verify(modelMapper, times(1)).map(any(), eq(LocationTranslationDTO.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnSaveLocationTranslation() {
        List<LocationTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> service.saveTranslation(locationModel.getUuid(), returnData.get(0))
        );

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldUpdateLocation() throws IOException {
        LocationDTO patch = new LocationDTO();
        patch.setUuid(locationModel.getUuid());
        patch.setHref("/api/v1/" + locationModel.getUuid());
        patch.setLanguage("en-US");
        locationModel.setHref("/api/v1/" + locationModel.getUuid());

        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.ofNullable(locationModel));
        when(modelMapper.map(any(), eq(LocationModel.class))).thenReturn(locationModel);
        when(utils.merge(any(LocationModel.class), any(LocationDTO.class))).thenReturn(locationModel);
        when(repository.save(any(LocationModel.class))).thenReturn(locationModel);

        service.patch(locationModel.getUuid(), patch);

        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), any());
        verify(utils, times(1)).merge(any(LocationModel.class), any(LocationDTO.class));
        verify(repository, times(1)).save(any(LocationModel.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnUpdateLocation() {
        LocationDTO patch = new LocationDTO();

        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patch(locationModel.getUuid(), patch));

        verify(repository, times(1))
                .findByUuidAndTranslationLanguage(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldUpdateLocationTranslation() throws IOException {
        List<LocationTranslation> data = Collections.singletonList(translation);
        LocationTranslationDTO patch = new LocationTranslationDTO();
        patch.setName("Lorem Ipsum");

        when(translationRepository.findByLocationUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        data.get(0).setName("Lorem Ipsum");
        when(utils.merge(any(LocationTranslation.class), any(LocationTranslationDTO.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(LocationTranslation.class))).thenReturn(data.get(0));

        service.patchTranslation(locationModel.getUuid(), "en-US", patch);

        verify(translationRepository, times(1)).findByLocationUuidAndLanguage(any(UUID.class), anyString());
        verify(utils, times(1)).merge(any(LocationTranslation.class), any(LocationTranslationDTO.class));
        verify(translationRepository, times(1)).save(any(LocationTranslation.class));
    }

    @Test
    void whenNoLocationTranslationFoundShouldThrowItemNotFoundExceptionOnUpdateLocationTranslation() {
        LocationTranslationDTO patch = new LocationTranslationDTO();

        when(translationRepository.findByLocationUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.patchTranslation(locationModel.getUuid(), "en-US", patch)
        );

        verify(translationRepository, times(1)).findByLocationUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldDeleteLocation() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.deleteById(locationModel.getUuid());

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoLocationFoundShouldThrowItemNotFoundExceptionOnDeleteLocation() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteById(locationModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldDeleteLocationTranslation() {
        when(translationRepository.existsByLocationUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);
        doNothing().when(translationRepository).deleteByLocationUuidAndLanguage(any(UUID.class), anyString());

        service.deleteTranslation(locationModel.getUuid(), "en-US");

        verify(translationRepository, times(1)).existsByLocationUuidAndLanguage(any(UUID.class), anyString());
        verify(translationRepository, times(1)).deleteByLocationUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void whenNoLocationTranslationFoundShouldThrowItemNotFoundExceptionOnDeleteLocationTranslation() {
        when(translationRepository.existsByLocationUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteTranslation(locationModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).existsByLocationUuidAndLanguage(any(UUID.class), anyString());
    }
}