package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.core.LanguageUtils;
import com.lucasjosino.hawapi.core.StringUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.GameFilter;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.dto.GameDTO;
import com.lucasjosino.hawapi.models.dto.translation.GameTranslationDTO;
import com.lucasjosino.hawapi.models.translations.GameTranslation;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.GameRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.repositories.translation.GameTranslationRepository;
import com.lucasjosino.hawapi.services.GameService;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    private static final SpecificationBuilder<GameModel> spec = new SpecificationBuilder<>();

    private static final Random random = new Random();

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final LanguageUtils languageUtils;

    private final GameRepository repository;

    private final GameTranslationRepository translationRepository;

    @Autowired
    public GameServiceImpl(
            GameRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            LanguageUtils languageUtils, GameTranslationRepository translationRepository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.languageUtils = languageUtils;
        this.translationRepository = translationRepository;
        this.basePath = config.getApiBaseUrl() + "/games";
    }

    @Transactional
    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    @Transactional
    public List<GameDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<GameModel> res = repository.findAll(spec.with(filters, GameFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, GameDTO[].class));
    }

    @Transactional
    public GameDTO findRandom(String language) {
        long count = repository.count();
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<GameModel> page = repository.findAll(spec.withTranslation(language), singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), GameDTO.class);
    }

    @Transactional
    public List<GameTranslationDTO> findAllTranslationsBy(UUID uuid) {
        List<GameTranslation> res = translationRepository.findAllByGameUuid(uuid);
        return Arrays.asList(modelMapper.map(res, GameTranslationDTO[].class));
    }

    @Transactional
    public GameTranslationDTO findRandomTranslation(UUID uuid) {
        long count = repository.count();
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<GameTranslation> page = translationRepository.findAllByGameUuid(uuid, singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), GameTranslationDTO.class);
    }

    @Transactional
    public GameDTO findBy(UUID uuid, String language) {
        GameModel res = repository
                .findByUuidAndTranslationLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, GameDTO.class);
    }

    @Transactional
    public GameTranslationDTO findTranslationBy(UUID uuid, String language) {
        GameTranslation res = translationRepository
                .findByGameUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, GameTranslationDTO.class);
    }

    @Transactional
    public GameDTO save(GameDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);
        dto.setLanguages(new String[]{dto.getLanguage()});

        validateDTO(uuid, dto.getLanguage());

        GameModel dtoToModel = modelMapper.map(dto, GameModel.class);
        dtoToModel.getTranslation().setGameUuid(uuid);

        GameModel res = repository.save(dtoToModel);

        return modelMapper.map(res, GameDTO.class);
    }

    @Transactional
    public GameTranslationDTO saveTranslation(UUID uuid, GameTranslationDTO dto) {
        validateDTO(uuid, dto.getLanguage());

        GameTranslation dtoToModel = modelMapper.map(dto, GameTranslation.class);
        dtoToModel.setGameUuid(uuid);

        GameTranslation res = translationRepository.save(dtoToModel);

        return modelMapper.map(res, GameTranslationDTO.class);
    }

    @Transactional
    public void patch(UUID uuid, GameDTO patch) throws IOException {
        GameModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        GameModel dtoToModel = modelMapper.map(dbRes, GameModel.class);
        GameModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    @Transactional
    public void patchTranslation(UUID uuid, String language, GameTranslationDTO patch) throws IOException {
        GameTranslation translation = translationRepository.findByGameUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);

        GameTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setGameUuid(uuid);

        translationRepository.save(patchedTranslation);
    }

    @Transactional
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    @Transactional
    public void deleteTranslation(UUID uuid, String language) {
        if (!translationRepository.existsByGameUuidAndLanguage(uuid, language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteByGameUuidAndLanguage(uuid, language);
    }

    private void validateDTO(UUID uuid, String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            throw new BadRequestException("Column 'language' is required");
        }

        if (!languageUtils.isSupportedLanguage(language)) {
            throw new BadRequestException("Language '" + language + "' is currently not supported!");
        }

        if (translationRepository.existsByGameUuidAndLanguage(uuid, language)) {
            throw new SaveConflictException("Language '" + language + "' already exist!");
        }
    }
}