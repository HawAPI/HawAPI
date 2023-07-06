package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.dto.SeasonDTO;
import com.lucasjosino.hawapi.models.dto.translation.SeasonTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import com.lucasjosino.hawapi.repositories.translation.SeasonTranslationRepository;
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
class SeasonServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private SeasonModel seasonModel;

    private SeasonDTO seasonDTO;

    private SeasonTranslation translation;

    private SeasonTranslationDTO translationDTO;

    @InjectMocks
    private SeasonServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private Random random;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SeasonRepository repository;

    @Mock
    private SeasonTranslationRepository translationRepository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        seasonDTO = new SeasonDTO();
        seasonDTO.setUuid(UUID.randomUUID());
        seasonDTO.setHref("/api/v1/seasons/" + seasonDTO.getUuid());
        seasonDTO.setLanguages(Collections.singletonList("Lorem"));
        seasonDTO.setDurationTotal(215398753);
        seasonDTO.setSeasonNum((byte) 2);
        seasonDTO.setReleaseDate(LocalDate.now());
        seasonDTO.setNextSeason("/api/v1/seasons/3");
        seasonDTO.setPrevSeason("/api/v1/seasons/1");
        seasonDTO.setEpisodes(Arrays.asList("/api/v1/episodes/1", "/api/v1/episodes/2", "/api/v1/episodes/3"));
        seasonDTO.setSoundtracks(Arrays.asList("/api/v1/soundtracks/1",
                "/api/v1/soundtracks/2",
                "/api/v1/soundtracks/3"
        ));
        seasonDTO.setBudget(218459);
        seasonDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        seasonDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        seasonDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        seasonDTO.setCreatedAt(LocalDateTime.now());
        seasonDTO.setUpdatedAt(LocalDateTime.now());
        seasonDTO.setLanguage("en-US");
        seasonDTO.setTitle("Lorem Ipsum");
        seasonDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        seasonDTO.setGenres(Arrays.asList("gen1", "gen2", "gen3"));
        seasonDTO.setTrailers(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));

        translationDTO = new SeasonTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setTitle("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translationDTO.setGenres(Arrays.asList("gen1", "gen2", "gen3"));
        translationDTO.setTrailers(Arrays.asList("https://youtube.com/watch?v=1", "https://youtube.com/watch?v=2"));

        seasonModel = mapper.map(seasonDTO, SeasonModel.class);
        translation = mapper.map(translationDTO, SeasonTranslation.class);
        translation.setSeasonUuid(seasonModel.getUuid());

        seasonModel.setTranslation(translation);
    }

    @Test
    void shouldReturnAllSeasonUUIDs() {
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
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllSeasonUUIDs() {
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
    void shouldReturnAllSeasons() {
        List<UUID> uuids = Collections.singletonList(seasonModel.getUuid());
        List<SeasonModel> data = Collections.singletonList(seasonModel);
        SeasonDTO[] returnData = {seasonDTO};

        when(repository.findAll(Mockito.<Specification<SeasonModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), any())).thenReturn(returnData);

        List<SeasonDTO> res = service.findAll(new HashMap<>(), uuids);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(seasonDTO, res.get(0));
        verify(repository, times(1)).findAll(Mockito.<Specification<SeasonModel>>any());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllSeasons() {
        List<UUID> uuids = Collections.emptyList();
        List<SeasonModel> data = Collections.emptyList();
        SeasonDTO[] returnData = {};

        when(repository.findAll(Mockito.<Specification<SeasonModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(SeasonDTO[].class))).thenReturn(returnData);

        List<SeasonDTO> res = service.findAll(new HashMap<>(), uuids);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll(Mockito.<Specification<SeasonModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(SeasonDTO[].class));
    }

    @Test
    void shouldReturnAllSeasonTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllBySeasonUuid(any(UUID.class)))
                .thenReturn(Collections.singletonList(translation));
        when(modelMapper.map(anyList(), eq(SeasonTranslationDTO[].class)))
                .thenReturn(new SeasonTranslationDTO[]{translationDTO});

        List<SeasonTranslationDTO> res = service.findAllTranslationsBy(seasonModel.getUuid());

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(translationDTO, res.get(0));
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllBySeasonUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(anyList(), eq(SeasonTranslationDTO[].class));
    }

    @Test
    void whenNoTranslationIsFoundShouldReturnEmptyListOnReturnAllSeasonTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllBySeasonUuid(any(UUID.class))).thenReturn(Collections.emptyList());
        when(modelMapper.map(any(), eq(SeasonTranslationDTO[].class))).thenReturn(new SeasonTranslationDTO[]{});

        List<SeasonTranslationDTO> res = service.findAllTranslationsBy(seasonModel.getUuid());

        assertTrue(res.isEmpty());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllBySeasonUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(SeasonTranslationDTO[].class));
    }

    @Test
    void whenNoSeasonIsFoundShouldThrowItemNotFoundExceptionOnReturnAllSeasonTranslations() {
        when(translationRepository.findBySeasonUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(seasonModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).findBySeasonUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnRandomSeason() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());
        List<SeasonModel> data = Collections.singletonList(seasonModel);
        Page<SeasonModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);

        when(repository.count()).thenReturn((long) uuids.size());
        when(utils.getCountOrThrow(anyLong())).thenReturn((long) uuids.size());
        when(random.nextInt(anyInt())).thenReturn(1);
        when(repository.findAll(Mockito.<Specification<SeasonModel>>any(), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), any())).thenReturn(seasonDTO);

        SeasonDTO res = service.findRandom("en-US");

        assertNotNull(res);
        assertEquals(seasonDTO, res);
        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(repository, times(1)).findAll(
                Mockito.<Specification<SeasonModel>>any(),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnReturnRandomSeason() {
        when(repository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandom("en-US"));

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnRandomSeasonTranslation() {
        long count = 1;
        List<SeasonTranslation> data = Collections.singletonList(translation);
        Page<SeasonTranslation> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);
        List<SeasonTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn(count);
        when(utils.getCountOrThrow(anyLong())).thenReturn(count);
        when(random.nextInt(anyInt())).thenReturn(1);
        when(translationRepository.findAllBySeasonUuid(any(UUID.class), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(SeasonTranslationDTO.class))).thenReturn(returnData.get(0));

        SeasonTranslationDTO res = service.findRandomTranslation(seasonModel.getUuid());

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(translationRepository, times(1)).findAllBySeasonUuid(
                any(UUID.class),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), eq(SeasonTranslationDTO.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnReturnRandomSeasonTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(seasonModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void whenNoSeasonTranslationFoundShouldThrowItemNotFoundExceptionOnReturnRandomSeasonTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(seasonModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnSeasonByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.ofNullable(seasonModel));
        when(modelMapper.map(any(), any())).thenReturn(seasonDTO);

        SeasonDTO res = service.findBy(seasonModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(seasonDTO, res);
        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnReturnSeasonByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findBy(seasonModel.getUuid(), "en-US"));

        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnSeasonTranslationById() {
        List<SeasonTranslation> data = Collections.singletonList(translation);
        List<SeasonTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(translationRepository.findBySeasonUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        when(modelMapper.map(any(), eq(SeasonTranslationDTO.class))).thenReturn(returnData.get(0));

        SeasonTranslationDTO res = service.findTranslationBy(seasonModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(translationRepository, times(1))
                .findBySeasonUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(SeasonTranslationDTO.class));
    }

    @Test
    void whenNoSeasonTranslationFoundShouldThrowItemNotFoundExceptionOnReturnSeasonTranslationById() {
        when(translationRepository.findBySeasonUuidAndLanguage(any(UUID.class),
                anyString()
        )).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(seasonModel.getUuid(), "en-US"));

        verify(translationRepository, times(1))
                .findBySeasonUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveSeason() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.existsBySeasonUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);
        when(modelMapper.map(any(), eq(SeasonModel.class))).thenReturn(seasonModel);
        when(repository.save(any(SeasonModel.class))).thenReturn(seasonModel);
        when(modelMapper.map(any(), eq(SeasonDTO.class))).thenReturn(seasonDTO);

        SeasonDTO res = service.save(seasonDTO);

        assertNotNull(res);
        assertEquals(seasonDTO, res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1))
                .existsBySeasonUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(SeasonModel.class));
        verify(repository, times(1)).save(any(SeasonModel.class));
        verify(modelMapper, times(1)).map(any(), eq(SeasonDTO.class));
    }

    @Test
    void whenInvalidLanguageIsProvidedShouldThrowBadRequestExceptionOnSaveSeason() {
        SeasonDTO newSeason = new SeasonDTO();
        newSeason.setLanguage("");

        assertThrows(BadRequestException.class, () -> service.save(newSeason));
    }

    @Test
    void whenTranslationAlreadyFoundShouldThrowSaveConflictExceptionOnSaveSeason() {
        SeasonDTO newSeason = new SeasonDTO();
        newSeason.setLanguage("en-US");

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.existsBySeasonUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);

        assertThrows(SaveConflictException.class, () -> service.save(newSeason));

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).existsBySeasonUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveSeasonTranslation() {
        List<SeasonTranslation> data = Collections.singletonList(translation);
        List<SeasonTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(modelMapper.map(any(), eq(SeasonTranslation.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(SeasonTranslation.class))).thenReturn(data.get(0));
        when(modelMapper.map(any(), eq(SeasonTranslationDTO.class))).thenReturn(returnData.get(0));

        SeasonTranslationDTO res = service.saveTranslation(seasonModel.getUuid(), returnData.get(0));

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(SeasonTranslation.class));
        verify(translationRepository, times(1)).save(any(SeasonTranslation.class));
        verify(modelMapper, times(1)).map(any(), eq(SeasonTranslationDTO.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnSaveSeasonTranslation() {
        List<SeasonTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> service.saveTranslation(seasonModel.getUuid(), returnData.get(0))
        );

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldUpdateSeason() throws IOException {
        SeasonDTO patch = new SeasonDTO();
        patch.setUuid(seasonModel.getUuid());
        patch.setHref("/api/v1/" + seasonModel.getUuid());
        seasonModel.setHref("/api/v1/" + seasonModel.getUuid());

        when(repository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(seasonModel));
        when(modelMapper.map(any(), eq(SeasonModel.class))).thenReturn(seasonModel);
        when(utils.merge(any(SeasonModel.class), any(SeasonDTO.class))).thenReturn(seasonModel);
        when(repository.save(any(SeasonModel.class))).thenReturn(seasonModel);

        service.patch(seasonModel.getUuid(), patch);

        verify(repository, times(1)).findById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), any());
        verify(utils, times(1)).merge(any(SeasonModel.class), any(SeasonDTO.class));
        verify(repository, times(1)).save(any(SeasonModel.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnUpdateSeason() {
        SeasonDTO patch = new SeasonDTO();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patch(seasonModel.getUuid(), patch));

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldUpdateSeasonTranslation() throws IOException {
        List<SeasonTranslation> data = Collections.singletonList(translation);
        SeasonTranslationDTO patch = new SeasonTranslationDTO();
        patch.setTitle("Lorem Ipsum");

        when(translationRepository.findBySeasonUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        data.get(0).setTitle("Lorem Ipsum");
        when(utils.merge(any(SeasonTranslation.class), any(SeasonTranslationDTO.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(SeasonTranslation.class))).thenReturn(data.get(0));

        service.patchTranslation(seasonModel.getUuid(), "en-US", patch);

        verify(translationRepository, times(1)).findBySeasonUuidAndLanguage(any(UUID.class), anyString());
        verify(utils, times(1)).merge(any(SeasonTranslation.class), any(SeasonTranslationDTO.class));
        verify(translationRepository, times(1)).save(any(SeasonTranslation.class));
    }

    @Test
    void whenNoSeasonTranslationFoundShouldThrowItemNotFoundExceptionOnUpdateSeasonTranslation() {
        SeasonTranslationDTO patch = new SeasonTranslationDTO();

        // TODO: Remove duplicate code
        when(translationRepository.findBySeasonUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.patchTranslation(seasonModel.getUuid(), "en-US", patch)
        );

        verify(translationRepository, times(1)).findBySeasonUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldDeleteSeason() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.deleteById(seasonModel.getUuid());

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoSeasonFoundShouldThrowItemNotFoundExceptionOnDeleteSeason() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteById(seasonModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldDeleteSeasonTranslation() {
        when(translationRepository.existsBySeasonUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);
        doNothing().when(translationRepository).deleteBySeasonUuidAndLanguage(any(UUID.class), anyString());

        service.deleteTranslation(seasonModel.getUuid(), "en-US");

        verify(translationRepository, times(1)).existsBySeasonUuidAndLanguage(any(UUID.class), anyString());
        verify(translationRepository, times(1)).deleteBySeasonUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void whenNoSeasonTranslationFoundShouldThrowItemNotFoundExceptionOnDeleteSeasonTranslation() {
        when(translationRepository.existsBySeasonUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteTranslation(seasonModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).existsBySeasonUuidAndLanguage(any(UUID.class), anyString());
    }
}