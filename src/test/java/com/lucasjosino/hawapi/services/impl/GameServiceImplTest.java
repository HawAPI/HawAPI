package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.dto.GameDTO;
import com.lucasjosino.hawapi.models.dto.translation.GameTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.GameTranslation;
import com.lucasjosino.hawapi.repositories.GameRepository;
import com.lucasjosino.hawapi.repositories.translation.GameTranslationRepository;
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
class GameServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private GameModel gameModel;

    private GameDTO gameDTO;

    private GameTranslation translation;

    private GameTranslationDTO translationDTO;

    @InjectMocks
    private GameServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private Random random;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private GameRepository repository;

    @Mock
    private GameTranslationRepository translationRepository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        gameDTO = new GameDTO();
        gameDTO.setUuid(UUID.randomUUID());
        gameDTO.setHref("/api/v1/games/" + gameDTO.getUuid());
        gameDTO.setLanguages(Collections.singletonList("Lorem"));
        gameDTO.setReleaseDate(LocalDate.now());
        gameDTO.setWebsite("https://example.com");
        gameDTO.setPlaytime(210574565);
        gameDTO.setAgeRating("100+");
        gameDTO.setStores(Arrays.asList("https://store.example.com", "https://store.example.com"));
        gameDTO.setModes(Arrays.asList("Single Player", "Multi Player"));
        gameDTO.setPublishers(Arrays.asList("Lorem", "Ipsum"));
        gameDTO.setDevelopers(Arrays.asList("Lorem", "Ipsum"));
        gameDTO.setPlatforms(Arrays.asList("Android", "iOS"));
        gameDTO.setGenres(Arrays.asList("Lorem", "Ipsum"));
        gameDTO.setTags(Arrays.asList("horror", "suspense"));
        gameDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        gameDTO.setImages(Arrays.asList("https://example.com/image.jpg", "https://example.com/image.jpg"));
        gameDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        gameDTO.setCreatedAt(LocalDateTime.now());
        gameDTO.setUpdatedAt(LocalDateTime.now());
        gameDTO.setLanguage("en-US");
        gameDTO.setName("Lorem Ipsum");
        gameDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        gameDTO.setTrailer("https://youtube.com/watch?v=1");

        translationDTO = new GameTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setName("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        translationDTO.setTrailer("https://youtube.com/watch?v=1");

        gameModel = mapper.map(gameDTO, GameModel.class);
        translation = mapper.map(translationDTO, GameTranslation.class);
        translation.setGameUuid(gameModel.getUuid());

        gameModel.setTranslation(translation);
    }

    @Test
    void shouldReturnAllGameUUIDs() {
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
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllGameUUIDs() {
        List<UUID> uuids = Collections.emptyList();

        when(repository.findAllUUIDs(any(Pageable.class))).thenReturn(uuids);

        Pageable pageable = Pageable.ofSize(1);
        Page<UUID> res = service.findAllUUIDs(pageable, 0);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAllUUIDs(any(Pageable.class));
    }

    @Test
    void shouldReturnAllGames() {
        List<UUID> uuids = Collections.singletonList(gameModel.getUuid());
        List<GameModel> data = Collections.singletonList(gameModel);
        GameDTO[] returnData = {gameDTO};

        when(repository.findAll(Mockito.<Specification<GameModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), any())).thenReturn(returnData);

        List<GameDTO> res = service.findAll(new HashMap<>(), uuids);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(gameDTO, res.get(0));
        verify(repository, times(1)).findAll(Mockito.<Specification<GameModel>>any());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoUUIDIsFoundShouldReturnEmptyListOnReturnAllGames() {
        List<UUID> uuids = Collections.emptyList();
        List<GameModel> data = Collections.emptyList();
        GameDTO[] returnData = {};

        when(repository.findAll(Mockito.<Specification<GameModel>>any())).thenReturn(data);
        when(modelMapper.map(any(), eq(GameDTO[].class))).thenReturn(returnData);

        List<GameDTO> res = service.findAll(new HashMap<>(), uuids);

        assertTrue(res.isEmpty());
        verify(repository, times(1)).findAll(Mockito.<Specification<GameModel>>any());
        verify(modelMapper, times(1)).map(any(), eq(GameDTO[].class));
    }

    @Test
    void shouldReturnAllGameTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllByGameUuid(any(UUID.class)))
                .thenReturn(Collections.singletonList(translation));
        when(modelMapper.map(anyList(), eq(GameTranslationDTO[].class)))
                .thenReturn(new GameTranslationDTO[]{translationDTO});

        List<GameTranslationDTO> res = service.findAllTranslationsBy(gameModel.getUuid());

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(translationDTO, res.get(0));
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllByGameUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(anyList(), eq(GameTranslationDTO[].class));
    }

    @Test
    void whenNoTranslationIsFoundShouldReturnEmptyListOnReturnAllGameTranslations() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.findAllByGameUuid(any(UUID.class))).thenReturn(Collections.emptyList());
        when(modelMapper.map(any(), eq(GameTranslationDTO[].class))).thenReturn(new GameTranslationDTO[]{});

        List<GameTranslationDTO> res = service.findAllTranslationsBy(gameModel.getUuid());

        assertTrue(res.isEmpty());
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).findAllByGameUuid(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(GameTranslationDTO[].class));
    }

    @Test
    void whenNoGameIsFoundShouldThrowItemNotFoundExceptionOnReturnAllGameTranslations() {
        when(translationRepository.findByGameUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(gameModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).findByGameUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnRandomGame() {
        List<UUID> uuids = Collections.singletonList(UUID.randomUUID());
        List<GameModel> data = Collections.singletonList(gameModel);
        Page<GameModel> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);

        when(repository.count()).thenReturn((long) uuids.size());
        when(utils.getCountOrThrow(anyLong())).thenReturn((long) uuids.size());
        when(random.nextInt(anyInt())).thenReturn(1);
        when(repository.findAll(Mockito.<Specification<GameModel>>any(), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), any())).thenReturn(gameDTO);

        GameDTO res = service.findRandom("en-US");

        assertNotNull(res);
        assertEquals(gameDTO, res);
        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(repository, times(1)).findAll(
                Mockito.<Specification<GameModel>>any(),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnReturnRandomGame() {
        when(repository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandom("en-US"));

        verify(repository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnRandomGameTranslation() {
        long count = 1;
        List<GameTranslation> data = Collections.singletonList(translation);
        Page<GameTranslation> page = PageableExecutionUtils.getPage(data, Pageable.ofSize(1), data::size);
        List<GameTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn(count);
        when(utils.getCountOrThrow(anyLong())).thenReturn(count);
        when(random.nextInt(anyInt())).thenReturn(1);
        when(translationRepository.findAllByGameUuid(any(UUID.class), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(GameTranslationDTO.class))).thenReturn(returnData.get(0));

        GameTranslationDTO res = service.findRandomTranslation(gameModel.getUuid());

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
        verify(random, times(1)).nextInt(anyInt());
        verify(translationRepository, times(1)).findAllByGameUuid(
                any(UUID.class),
                any(PageRequest.class)
        );
        verify(modelMapper, times(1)).map(any(), eq(GameTranslationDTO.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnReturnRandomGameTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(gameModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void whenNoGameTranslationFoundShouldThrowItemNotFoundExceptionOnReturnRandomGameTranslation() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(translationRepository.count()).thenReturn((long) 0);
        when(utils.getCountOrThrow(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findRandomTranslation(gameModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(translationRepository, times(1)).count();
        verify(utils, times(1)).getCountOrThrow(anyLong());
    }

    @Test
    void shouldReturnGameByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.ofNullable(gameModel));
        when(modelMapper.map(any(), any())).thenReturn(gameDTO);

        GameDTO res = service.findBy(gameModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(gameDTO, res);
        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), any());
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnReturnGameByUUID() {
        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findBy(gameModel.getUuid(), "en-US"));

        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldReturnGameTranslationById() {
        List<GameTranslation> data = Collections.singletonList(translation);
        List<GameTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(translationRepository.findByGameUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        when(modelMapper.map(any(), eq(GameTranslationDTO.class))).thenReturn(returnData.get(0));

        GameTranslationDTO res = service.findTranslationBy(gameModel.getUuid(), "en-US");

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(translationRepository, times(1))
                .findByGameUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(GameTranslationDTO.class));
    }

    @Test
    void whenNoGameTranslationFoundShouldThrowItemNotFoundExceptionOnReturnGameTranslationById() {
        when(translationRepository.findByGameUuidAndLanguage(any(UUID.class),
                anyString()
        )).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.findTranslationBy(gameModel.getUuid(), "en-US"));

        verify(translationRepository, times(1))
                .findByGameUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveGame() {
        when(translationRepository.existsByGameUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);
        when(modelMapper.map(any(), eq(GameModel.class))).thenReturn(gameModel);
        when(repository.save(any(GameModel.class))).thenReturn(gameModel);
        when(modelMapper.map(any(), eq(GameDTO.class))).thenReturn(gameDTO);

        GameDTO res = service.save(gameDTO);

        assertNotNull(res);
        assertEquals(gameDTO, res);
        verify(translationRepository, times(1))
                .existsByGameUuidAndLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), eq(GameModel.class));
        verify(repository, times(1)).save(any(GameModel.class));
        verify(modelMapper, times(1)).map(any(), eq(GameDTO.class));
    }

    @Test
    void whenInvalidLanguageIsProvidedShouldThrowBadRequestExceptionOnSaveGame() {
        GameDTO newGame = new GameDTO();
        newGame.setLanguage("");

        assertThrows(BadRequestException.class, () -> service.save(newGame));
    }

    @Test
    void whenTranslationAlreadyExistsShouldThrowSaveConflictExceptionOnSaveGame() {
        GameDTO newGame = new GameDTO();
        newGame.setLanguage("en-US");

        when(translationRepository.existsByGameUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);

        assertThrows(SaveConflictException.class, () -> service.save(newGame));

        verify(translationRepository, times(1)).existsByGameUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldSaveGameTranslation() {
        List<GameTranslation> data = Collections.singletonList(translation);
        List<GameTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(true);
        when(modelMapper.map(any(), eq(GameTranslation.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(GameTranslation.class))).thenReturn(data.get(0));
        when(modelMapper.map(any(), eq(GameTranslationDTO.class))).thenReturn(returnData.get(0));

        GameTranslationDTO res = service.saveTranslation(gameModel.getUuid(), returnData.get(0));

        assertNotNull(res);
        assertEquals(returnData.get(0), res);
        verify(repository, times(1)).existsById(any(UUID.class));
        verify(modelMapper, times(1)).map(any(), eq(GameTranslation.class));
        verify(translationRepository, times(1)).save(any(GameTranslation.class));
        verify(modelMapper, times(1)).map(any(), eq(GameTranslationDTO.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnSaveGameTranslation() {
        List<GameTranslationDTO> returnData = Collections.singletonList(translationDTO);

        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> service.saveTranslation(gameModel.getUuid(), returnData.get(0))
        );

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldUpdateGame() throws IOException {
        GameDTO patch = new GameDTO();
        patch.setUuid(gameModel.getUuid());
        patch.setHref("/api/v1/" + gameModel.getUuid());
        patch.setLanguage("en-US");
        gameModel.setHref("/api/v1/" + gameModel.getUuid());

        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.ofNullable(gameModel));
        when(modelMapper.map(any(), eq(GameModel.class))).thenReturn(gameModel);
        when(utils.merge(any(GameModel.class), any(GameDTO.class))).thenReturn(gameModel);
        when(repository.save(any(GameModel.class))).thenReturn(gameModel);

        service.patch(gameModel.getUuid(), patch);

        verify(repository, times(1)).findByUuidAndTranslationLanguage(any(UUID.class), anyString());
        verify(modelMapper, times(1)).map(any(), any());
        verify(utils, times(1)).merge(any(GameModel.class), any(GameDTO.class));
        verify(repository, times(1)).save(any(GameModel.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnUpdateGame() {
        GameDTO patch = new GameDTO();

        when(repository.findByUuidAndTranslationLanguage(any(UUID.class), anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patch(gameModel.getUuid(), patch));

        verify(repository, times(1))
                .findByUuidAndTranslationLanguage(any(UUID.class), nullable(String.class));
    }

    @Test
    void shouldUpdateGameTranslation() throws IOException {
        List<GameTranslation> data = Collections.singletonList(translation);
        GameTranslationDTO patch = new GameTranslationDTO();
        patch.setName("Lorem Ipsum");

        when(translationRepository.findByGameUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.of(data.get(0)));
        data.get(0).setName("Lorem Ipsum");
        when(utils.merge(any(GameTranslation.class), any(GameTranslationDTO.class))).thenReturn(data.get(0));
        when(translationRepository.save(any(GameTranslation.class))).thenReturn(data.get(0));

        service.patchTranslation(gameModel.getUuid(), "en-US", patch);

        verify(translationRepository, times(1)).findByGameUuidAndLanguage(any(UUID.class), anyString());
        verify(utils, times(1)).merge(any(GameTranslation.class), any(GameTranslationDTO.class));
        verify(translationRepository, times(1)).save(any(GameTranslation.class));
    }

    @Test
    void whenNoGameTranslationFoundShouldThrowItemNotFoundExceptionOnUpdateGameTranslation() {
        GameTranslationDTO patch = new GameTranslationDTO();

        when(translationRepository.findByGameUuidAndLanguage(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.patchTranslation(gameModel.getUuid(), "en-US", patch)
        );

        verify(translationRepository, times(1)).findByGameUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void shouldDeleteGame() {
        when(repository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(repository).deleteById(any(UUID.class));

        service.deleteById(gameModel.getUuid());

        verify(repository, times(1)).existsById(any(UUID.class));
        verify(repository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void whenNoGameFoundShouldThrowItemNotFoundExceptionOnDeleteGame() {
        when(repository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteById(gameModel.getUuid()));

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void shouldDeleteGameTranslation() {
        when(translationRepository.existsByGameUuidAndLanguage(any(UUID.class), anyString())).thenReturn(true);
        doNothing().when(translationRepository).deleteByGameUuidAndLanguage(any(UUID.class), anyString());

        service.deleteTranslation(gameModel.getUuid(), "en-US");

        verify(translationRepository, times(1)).existsByGameUuidAndLanguage(any(UUID.class), anyString());
        verify(translationRepository, times(1)).deleteByGameUuidAndLanguage(any(UUID.class), anyString());
    }

    @Test
    void whenNoGameTranslationFoundShouldThrowItemNotFoundExceptionOnDeleteGameTranslation() {
        when(translationRepository.existsByGameUuidAndLanguage(any(UUID.class), anyString())).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteTranslation(gameModel.getUuid(), "en-US"));

        verify(translationRepository, times(1)).existsByGameUuidAndLanguage(any(UUID.class), anyString());
    }
}