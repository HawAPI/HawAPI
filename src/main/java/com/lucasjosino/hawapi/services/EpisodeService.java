package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.core.LanguageUtils;
import com.lucasjosino.hawapi.core.StringUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import com.lucasjosino.hawapi.models.dto.translation.EpisodeTranslationDTO;
import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.repositories.translation.EpisodeTranslationRepository;
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
public class EpisodeService {

    private static final SpecificationBuilder<EpisodeModel> spec = new SpecificationBuilder<>();

    private static final Random random = new Random();

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final LanguageUtils languageUtils;

    private final EpisodeRepository repository;

    private final EpisodeTranslationRepository translationRepository;

    @Autowired
    public EpisodeService(
            EpisodeRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            LanguageUtils languageUtils, EpisodeTranslationRepository translationRepository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.languageUtils = languageUtils;
        this.translationRepository = translationRepository;
        this.basePath = config.getApiBaseUrl() + "/episodes";
    }

    @Transactional
    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    @Transactional
    public List<EpisodeDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<EpisodeModel> res = repository.findAll(spec.with(filters, EpisodeFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, EpisodeDTO[].class));
    }

    @Transactional
    public EpisodeDTO findRandom(String language) {
        long count = repository.count();
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<EpisodeModel> page = repository.findAll(spec.withTranslation(language), singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), EpisodeDTO.class);
    }

    @Transactional
    public List<EpisodeTranslationDTO> findAllTranslationsBy(UUID uuid) {
        List<EpisodeTranslation> res = translationRepository.findAllByEpisodeUuid(uuid);
        return Arrays.asList(modelMapper.map(res, EpisodeTranslationDTO[].class));
    }

    @Transactional
    public EpisodeTranslationDTO findRandomTranslation(UUID uuid) {
        long count = repository.count();
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<EpisodeTranslation> page = translationRepository.findAllByEpisodeUuid(uuid, singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), EpisodeTranslationDTO.class);
    }

    @Transactional
    public EpisodeDTO findBy(UUID uuid, String language) {
        EpisodeModel res = repository
                .findByUuidAndTranslationLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, EpisodeDTO.class);
    }

    @Transactional
    public EpisodeTranslationDTO findTranslationBy(UUID uuid, String language) {
        EpisodeTranslation res = translationRepository
                .findByEpisodeUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, EpisodeTranslationDTO.class);
    }

    @Transactional
    public EpisodeDTO save(EpisodeDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);
        dto.setLanguages(new String[]{dto.getLanguage()});

        validateDTO(uuid, dto.getLanguage());

        EpisodeModel dtoToModel = modelMapper.map(dto, EpisodeModel.class);
        dtoToModel.getTranslation().setEpisodeUuid(uuid);

        EpisodeModel res = repository.save(dtoToModel);

        return modelMapper.map(res, EpisodeDTO.class);
    }

    @Transactional
    public EpisodeTranslationDTO saveTranslation(UUID uuid, EpisodeTranslationDTO dto) {
        validateDTO(uuid, dto.getLanguage());

        EpisodeTranslation dtoToModel = modelMapper.map(dto, EpisodeTranslation.class);
        dtoToModel.setEpisodeUuid(uuid);

        EpisodeTranslation res = translationRepository.save(dtoToModel);

        return modelMapper.map(res, EpisodeTranslationDTO.class);
    }

    @Transactional
    public void patch(UUID uuid, EpisodeDTO patch) throws IOException {
        EpisodeModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        EpisodeModel dtoToModel = modelMapper.map(dbRes, EpisodeModel.class);
        EpisodeModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    @Transactional
    public void patchTranslation(UUID uuid, String language, EpisodeTranslationDTO patch) throws IOException {
        EpisodeTranslation translation = translationRepository.findByEpisodeUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);

        EpisodeTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setEpisodeUuid(uuid);

        translationRepository.save(patchedTranslation);
    }

    @Transactional
    public void delete(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    @Transactional
    public void deleteTranslation(UUID uuid, String language) {
        if (!translationRepository.existsByEpisodeUuidAndLanguage(uuid, language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteByEpisodeUuidAndLanguage(uuid, language);
    }

    private void validateDTO(UUID uuid, String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            throw new BadRequestException("Column 'language' is required");
        }

        if (!languageUtils.isSupportedLanguage(language)) {
            throw new BadRequestException("Language '" + language + "' is currently not supported!");
        }

        if (translationRepository.existsByEpisodeUuidAndLanguage(uuid, language)) {
            throw new SaveConflictException("Language '" + language + "' already exist!");
        }
    }
}
