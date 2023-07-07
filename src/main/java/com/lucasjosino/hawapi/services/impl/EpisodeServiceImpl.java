package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.EpisodeController;
import com.lucasjosino.hawapi.core.StringUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.EpisodeFilter;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.dto.EpisodeDTO;
import com.lucasjosino.hawapi.models.dto.translation.EpisodeTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.EpisodeTranslation;
import com.lucasjosino.hawapi.repositories.EpisodeRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.repositories.translation.EpisodeTranslationRepository;
import com.lucasjosino.hawapi.services.EpisodeService;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Methods to handle episodes
 *
 * @author Lucas Josino
 * @see EpisodeController
 * @since 1.0.0
 */
@Service
public class EpisodeServiceImpl implements EpisodeService {

    private static final SpecificationBuilder<EpisodeModel> spec = new SpecificationBuilder<>();

    private final Random random;

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final EpisodeRepository repository;

    private final EpisodeTranslationRepository translationRepository;

    @Autowired
    public EpisodeServiceImpl(
            Random random, EpisodeRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            EpisodeTranslationRepository translationRepository
    ) {
        this.random = random;
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.translationRepository = translationRepository;
        this.basePath = config.getApiBaseUrl() + "/episodes";
    }

    /**
     * Method that get all episode uuids filtering with {@link Pageable}
     *
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link Page} of {@link UUID} or empty
     * @since 1.0.0
     */
    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    /**
     * Method that get all episodes from the database
     *
     * @see EpisodeController#findAll(Map, Pageable)
     * @since 1.0.0
     */
    public List<EpisodeDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<EpisodeModel> res = repository.findAll(spec.with(filters, EpisodeFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, EpisodeDTO[].class));
    }

    /**
     * Method that get a single random episode from the database
     *
     * @see EpisodeController#findRandom(String)
     * @since 1.0.0
     */
    public EpisodeDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<EpisodeModel> page = repository.findAll(spec.withTranslation(language), singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), EpisodeDTO.class);
    }

    /**
     * Method that get all episode translations from the database
     *
     * @see EpisodeController#findAllTranslationsBy(UUID)
     * @since 1.0.0
     */
    public List<EpisodeTranslationDTO> findAllTranslationsBy(UUID uuid) {
        existsByIdOrThrow(uuid);

        List<EpisodeTranslation> res = translationRepository.findAllByEpisodeUuid(uuid);
        return Arrays.asList(modelMapper.map(res, EpisodeTranslationDTO[].class));
    }

    /**
     * Method that get a single random episode translation from the database
     *
     * @see EpisodeController#findRandomTranslation(UUID)
     * @since 1.0.0
     */
    public EpisodeTranslationDTO findRandomTranslation(UUID uuid) {
        existsByIdOrThrow(uuid);

        long count = utils.getCountOrThrow(translationRepository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<EpisodeTranslation> page = translationRepository.findAllByEpisodeUuid(uuid, singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), EpisodeTranslationDTO.class);
    }

    /**
     * Method that get a single episode from the database
     *
     * @see EpisodeController#findBy(UUID, String)
     * @since 1.0.0
     */
    public EpisodeDTO findBy(UUID uuid, String language) {
        EpisodeModel res = repository
                .findByUuidAndTranslationLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, EpisodeDTO.class);
    }

    /**
     * Method that get a single episode translation from the database
     *
     * @see EpisodeController#findTranslationBy(UUID, String)
     * @since 1.0.0
     */
    public EpisodeTranslationDTO findTranslationBy(UUID uuid, String language) {
        EpisodeTranslation res = translationRepository
                .findByEpisodeUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, EpisodeTranslationDTO.class);
    }

    /**
     * Method that crates an episode on the database
     *
     * @see EpisodeController#save(EpisodeDTO)
     * @since 1.0.0
     */
    public EpisodeDTO save(EpisodeDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);
        dto.setLanguages(Collections.singletonList(dto.getLanguage()));

        validateRequest(uuid, dto.getLanguage());

        EpisodeModel dtoToModel = modelMapper.map(dto, EpisodeModel.class);
        dtoToModel.getTranslation().setEpisodeUuid(uuid);

        EpisodeModel res = repository.save(dtoToModel);

        return modelMapper.map(res, EpisodeDTO.class);
    }

    /**
     * Method that crates an episode translation on the database
     *
     * @see EpisodeController#saveTranslation(UUID, EpisodeTranslationDTO)
     * @since 1.0.0
     */
    public EpisodeTranslationDTO saveTranslation(UUID uuid, EpisodeTranslationDTO dto) {
        if (!repository.existsById(uuid)) {
            throw new ItemNotFoundException("Item '" + uuid + "' doesn't exist!");
        }

        validateRequest(uuid, dto.getLanguage());

        EpisodeTranslation dtoToModel = modelMapper.map(dto, EpisodeTranslation.class);
        dtoToModel.setEpisodeUuid(uuid);

        EpisodeTranslation res = translationRepository.save(dtoToModel);

        return modelMapper.map(res, EpisodeTranslationDTO.class);
    }

    /**
     * Method that updates an episode on the database
     *
     * @see EpisodeController#patch(UUID, EpisodeDTO)
     * @since 1.0.0
     */
    public void patch(UUID uuid, EpisodeDTO patch) throws IOException {
        EpisodeModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        EpisodeModel dtoToModel = modelMapper.map(dbRes, EpisodeModel.class);
        EpisodeModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    /**
     * Method that updates an episode translation on the database
     *
     * @see EpisodeController#patchTranslation(UUID, String, EpisodeTranslationDTO)
     * @since 1.0.0
     */
    public void patchTranslation(UUID uuid, String language, EpisodeTranslationDTO patch) throws IOException {
        EpisodeTranslation translation = translationRepository.findByEpisodeUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);

        EpisodeTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setEpisodeUuid(uuid);

        translationRepository.save(patchedTranslation);
    }

    /**
     * Method that delete an episode from the database
     *
     * @see EpisodeController#delete(UUID)
     * @since 1.0.0
     */
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    /**
     * Method that delete an episode translation from the database
     *
     * @see EpisodeController#deleteTranslation(UUID, String)
     * @since 1.0.0
     */
    public void deleteTranslation(UUID uuid, String language) {
        if (!translationRepository.existsByEpisodeUuidAndLanguage(uuid, language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteByEpisodeUuidAndLanguage(uuid, language);
    }

    /**
     * Method to validate an request
     * <ul>
     *     <li>Validate if language is valid</li>
     *     <li>Validate if field with same uuid and language already exists</li>
     * </ul>
     *
     * @param uuid     An {@link UUID} to validate
     * @param language An {@link String} to validate
     * @throws BadRequestException   If <strong>language</strong> field is null or empty
     * @throws SaveConflictException If item already exists
     * @since 1.0.0
     */
    private void validateRequest(UUID uuid, String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            throw new BadRequestException("Column 'language' is required");
        }

        if (translationRepository.existsByEpisodeUuidAndLanguage(uuid, language)) {
            throw new SaveConflictException("Language '" + language + "' already exist!");
        }
    }

    /**
     * Method to validate an {@link UUID}
     *
     * @param uuid An {@link UUID} to validate
     * @throws ItemNotFoundException {@link UUID} doesn't exists
     */
    private void existsByIdOrThrow(UUID uuid) {
        if (repository.existsById(uuid)) return;
        throw new ItemNotFoundException("Episode '" + uuid + "' not found!");
    }
}
