package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.models.OverviewModel;
import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import com.lucasjosino.hawapi.models.dto.translation.OverviewTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.OverviewTranslation;
import com.lucasjosino.hawapi.repositories.OverviewRepository;
import com.lucasjosino.hawapi.repositories.translation.OverviewTranslationRepository;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OverviewServiceImplTest {

    private static final ModelMapper mapper = new ModelMapper();

    private OverviewModel overviewModel;

    private OverviewDTO overviewDTO;

    private OverviewTranslation translation;

    private OverviewTranslationDTO translationDTO;

    @InjectMocks
    private OverviewServiceImpl service;

    @Mock
    private ServiceUtils utils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private OverviewRepository repository;

    @Mock
    private OverviewTranslationRepository translationRepository;

    @Mock
    private OpenAPIProperty config;

    @BeforeEach
    void setUp() {
        overviewDTO = new OverviewDTO();
        overviewDTO.setUuid(UUID.randomUUID());
        overviewDTO.setHref("/api/v1/overview/" + overviewDTO.getUuid());
        overviewDTO.setCreators(Collections.singletonList("Lorem"));
        overviewDTO.setThumbnail("https://cdn.theproject.id/hawapi/image.jpg");
        overviewDTO.setSources(Arrays.asList("https://example.com", "https://example.com"));
        overviewDTO.setCreatedAt(LocalDateTime.now());
        overviewDTO.setUpdatedAt(LocalDateTime.now());
        overviewDTO.setLanguage("en-US");
        overviewDTO.setTitle("Lorem Ipsum");
        overviewDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        translationDTO = new OverviewTranslationDTO();
        translationDTO.setLanguage("en-US");
        translationDTO.setTitle("Lorem Ipsum");
        translationDTO.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        overviewModel = mapper.map(overviewDTO, OverviewModel.class);
        translation = mapper.map(translationDTO, OverviewTranslation.class);
        translation.setOverviewUuid(overviewModel.getUuid());

        overviewModel.setTranslation(translation);
    }

    @Test
    void shouldReturnAllOverviewTranslations() {
        List<OverviewTranslation> translations = Collections.singletonList(translation);
        OverviewTranslationDTO[] returnData = {translationDTO};

        when(translationRepository.findAll()).thenReturn(translations);
        when(modelMapper.map(any(), any())).thenReturn(returnData);

        List<OverviewTranslationDTO> res = service.findAllOverviewTranslations();

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        assertEquals(translationDTO, res.get(0));
        verify(translationRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(), eq(OverviewTranslationDTO[].class));
    }

    @Test
    void whenNoTranslationFoundShouldReturnEmptyListOnReturnAllOverviewTranslations() {
        List<OverviewTranslation> data = Collections.emptyList();
        OverviewTranslationDTO[] returnData = {};

        when(translationRepository.findAll()).thenReturn(data);
        when(modelMapper.map(any(), eq(OverviewTranslationDTO[].class))).thenReturn(returnData);

        List<OverviewTranslationDTO> res = service.findAllOverviewTranslations();

        assertTrue(res.isEmpty());
        verify(translationRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(), eq(OverviewTranslationDTO[].class));
    }

    @Test
    void shouldReturnOverview() {
        when(repository.findByTranslationLanguage(anyString())).thenReturn(Optional.ofNullable(overviewModel));
        when(modelMapper.map(any(), eq(OverviewDTO.class))).thenReturn(overviewDTO);

        OverviewDTO res = service.findOverviewBy("en-US");

        assertNotNull(res);
        assertEquals(overviewDTO, res);
        verify(repository, times(1)).findByTranslationLanguage(anyString());
        verify(modelMapper, times(1)).map(any(), eq(OverviewDTO.class));
    }

    @Test
    void whenNoOverviewFoundShouldReturnItemNotFoundExceptionOnReturnOverview() {
        when(repository.findByTranslationLanguage(anyString())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findOverviewBy("en-US"));

        verify(repository, times(1)).findByTranslationLanguage(anyString());
    }

    @Test
    void shouldReturnOverviewTranslationBy() {
        when(translationRepository.findByLanguage(anyString())).thenReturn(Optional.ofNullable(translation));
        when(modelMapper.map(any(), eq(OverviewTranslationDTO.class))).thenReturn(translationDTO);

        OverviewTranslationDTO res = service.findOverviewTranslationBy("en-US");

        assertNotNull(res);
        assertEquals(translationDTO, res);
        verify(translationRepository, times(1)).findByLanguage(anyString());
        verify(modelMapper, times(1)).map(any(), eq(OverviewTranslationDTO.class));
    }

    @Test
    void whenNoOverviewFoundShouldReturnItemNotFoundExceptionOnReturnOverviewTranslationBy() {
        when(translationRepository.findByLanguage(anyString())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.findOverviewTranslationBy("en-US"));

        verify(translationRepository, times(1)).findByLanguage(anyString());
    }

    @Test
    void shouldSaveOverview() {
        when(translationRepository.existsByLanguage(anyString())).thenReturn(false);
        when(modelMapper.map(any(), eq(OverviewModel.class))).thenReturn(overviewModel);
        when(repository.save(any(OverviewModel.class))).thenReturn(overviewModel);
        when(modelMapper.map(any(), eq(OverviewDTO.class))).thenReturn(overviewDTO);

        OverviewDTO res = service.saveOverview(overviewDTO);

        assertNotNull(res);
        assertEquals(overviewDTO, res);
        verify(translationRepository, times(1)).existsByLanguage(anyString());
        verify(modelMapper, times(1)).map(any(), eq(OverviewModel.class));
        verify(repository, times(1)).save(any(OverviewModel.class));
        verify(modelMapper, times(1)).map(any(), eq(OverviewDTO.class));
    }

    @Test
    void whenInvalidLanguageIsProvidedShouldThrowBadRequestExceptionOnSaveOverview() {
        OverviewDTO newOverview = new OverviewDTO();
        newOverview.setLanguage("");

        assertThrows(BadRequestException.class, () -> service.saveOverview(newOverview));
    }

    @Test
    void whenTranslationAlreadyExistsShouldThrowSaveConflictExceptionOnSaveOverview() {
        OverviewDTO newOverview = new OverviewDTO();
        newOverview.setLanguage("en-US");

        when(translationRepository.existsByLanguage(anyString())).thenReturn(true);

        assertThrows(SaveConflictException.class, () -> service.saveOverview(newOverview));

        verify(translationRepository, times(1)).existsByLanguage(anyString());
    }

    @Test
    void shouldSaveOverviewTranslation() {
        when(repository.findUUID()).thenReturn(Optional.of(overviewModel.getUuid().toString()));
        when(translationRepository.existsByLanguage(anyString())).thenReturn(false);
        when(modelMapper.map(any(), eq(OverviewTranslation.class))).thenReturn(translation);
        when(translationRepository.save(any(OverviewTranslation.class))).thenReturn(translation);
        when(modelMapper.map(any(), eq(OverviewTranslationDTO.class))).thenReturn(translationDTO);

        OverviewTranslationDTO res = service.saveOverviewTranslation("en-US", translationDTO);

        assertNotNull(res);
        assertEquals(translationDTO, res);
        verify(repository, times(1)).findUUID();
        verify(translationRepository, times(1)).existsByLanguage(anyString());
        verify(modelMapper, times(1)).map(any(), eq(OverviewTranslation.class));
        verify(translationRepository, times(1)).save(any(OverviewTranslation.class));
        verify(modelMapper, times(1)).map(any(), eq(OverviewTranslationDTO.class));
    }

    @Test
    void whenNoOverviewFoundShouldThrowItemNotFoundExceptionOnSaveOverviewTranslation() {
        OverviewTranslationDTO newOverview = new OverviewTranslationDTO();

        when(repository.findUUID()).thenReturn(Optional.empty());

        assertThrows(
                ItemNotFoundException.class,
                () -> service.saveOverviewTranslation("en-US", newOverview)
        );

        verify(repository, times(1)).findUUID();
    }

    @Test
    void whenTranslationAlreadyExistsShouldThrowSaveConflictExceptionOnSaveOverviewTranslation() {
        OverviewTranslationDTO newOverview = new OverviewTranslationDTO();
        newOverview.setLanguage("en-US");

        when(repository.findUUID()).thenReturn(Optional.of(overviewModel.getUuid().toString()));
        when(translationRepository.existsByLanguage(anyString())).thenReturn(true);

        assertThrows(
                SaveConflictException.class,
                () -> service.saveOverviewTranslation("en-US", newOverview)
        );

        verify(repository, times(1)).findUUID();
        verify(translationRepository, times(1)).existsByLanguage(anyString());
    }

    @Test
    void shouldPatchOverview() throws IOException {
        OverviewDTO patch = new OverviewDTO();
        patch.setLanguage("en-US");
        patch.setCreators(Arrays.asList("Lorem", "Ipsum"));
        overviewModel.setCreators(Arrays.asList("Lorem", "Ipsum"));

        when(repository.findByTranslationLanguage(anyString())).thenReturn(Optional.ofNullable(overviewModel));
        when(modelMapper.map(any(OverviewModel.class), eq(OverviewModel.class))).thenReturn(overviewModel);
        when(utils.merge(any(OverviewModel.class), any(OverviewDTO.class))).thenReturn(overviewModel);
        when(repository.save(any(OverviewModel.class))).thenReturn(overviewModel);

        service.patchOverview(patch);

        verify(repository, times(1)).findByTranslationLanguage(anyString());
        verify(modelMapper, times(1)).map(any(OverviewModel.class), eq(OverviewModel.class));
        verify(utils, times(1)).merge(any(OverviewModel.class), any(OverviewDTO.class));
        verify(repository, times(1)).save(any(OverviewModel.class));
    }

    @Test
    void whenNoOverviewFoundShouldThrowItemNotFoundExceptionOnPatchOverview() {
        OverviewDTO patch = new OverviewDTO();
        patch.setLanguage("en-US");

        when(repository.findByTranslationLanguage(anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patchOverview(patch));

        verify(repository, times(1)).findByTranslationLanguage(anyString());
    }

    @Test
    void shouldPatchOverviewTranslation() throws IOException {
        OverviewTranslationDTO patch = new OverviewTranslationDTO();
        patch.setTitle("Lorem Ipsum");

        when(translationRepository.findByLanguage(anyString())).thenReturn(Optional.ofNullable(translation));
        when(utils.merge(any(OverviewTranslation.class), any(OverviewTranslationDTO.class))).thenReturn(translation);
        when(translationRepository.save(any(OverviewTranslation.class))).thenReturn(translation);

        service.patchOverviewTranslation("en-US", patch);

        verify(translationRepository, times(1)).findByLanguage(anyString());
        verify(utils, times(1)).merge(any(OverviewTranslation.class), any(OverviewTranslationDTO.class));
        verify(translationRepository, times(1)).save(any(OverviewTranslation.class));
    }

    @Test
    void whenNoOverviewFoundShouldThrowItemNotFoundExceptionOnPatchOverviewTranslation() {
        OverviewTranslationDTO patch = new OverviewTranslationDTO();
        patch.setTitle("Lorem Ipsum");

        when(translationRepository.findByLanguage(anyString())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.patchOverviewTranslation("en-US", patch));

        verify(translationRepository, times(1)).findByLanguage(anyString());
    }

    @Test
    void shouldDeleteOverview() {
        doNothing().when(translationRepository).deleteAll();
        doNothing().when(repository).deleteAll();

        service.deleteOverview();

        verify(translationRepository, times(1)).deleteAll();
        verify(repository, times(1)).deleteAll();
    }

    @Test
    void shouldDeleteOverviewTranslation() {
        when(translationRepository.existsByLanguage(anyString())).thenReturn(true);
        doNothing().when(translationRepository).deleteByLanguage(anyString());

        service.deleteOverviewTranslation("en-US");

        verify(translationRepository, times(1)).existsByLanguage(anyString());
        verify(translationRepository, times(1)).deleteByLanguage(anyString());
    }

    @Test
    void whenNoOverviewFoundShouldThrowItemNotFoundExceptionOnDeleteOverviewTranslation() {
        when(translationRepository.existsByLanguage(anyString())).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteOverviewTranslation("en-US"));

        verify(translationRepository, times(1)).existsByLanguage(anyString());
    }
}