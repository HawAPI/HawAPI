package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.SeasonController;
import com.lucasjosino.hawapi.core.LanguageUtils;
import com.lucasjosino.hawapi.core.StringUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.SeasonFilter;
import com.lucasjosino.hawapi.models.SeasonModel;
import com.lucasjosino.hawapi.models.dto.SeasonDTO;
import com.lucasjosino.hawapi.models.dto.translation.SeasonTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.SeasonTranslation;
import com.lucasjosino.hawapi.repositories.SeasonRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.repositories.translation.SeasonTranslationRepository;
import com.lucasjosino.hawapi.services.SeasonService;
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
 * Methods to handle seasons
 *
 * @author Lucas Josino
 * @see SeasonController
 * @since 1.0.0
 */
@Service
public class SeasonServiceImpl implements SeasonService {

    private static final SpecificationBuilder<SeasonModel> spec = new SpecificationBuilder<>();

    private static final Random random = new Random();

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final LanguageUtils languageUtils;

    private final SeasonRepository repository;

    private final SeasonTranslationRepository translationRepository;

    @Autowired
    public SeasonServiceImpl(
            SeasonRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            LanguageUtils languageUtils, SeasonTranslationRepository translationRepository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.languageUtils = languageUtils;
        this.translationRepository = translationRepository;
        this.basePath = config.getApiBaseUrl() + "/seasons";
    }

    /**
     * Method that get all season uuids filtering with {@link Pageable}
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
     * Method that get all seasons from the database
     *
     * @see SeasonController#findAll(Map, Pageable)
     * @since 1.0.0
     */
    public List<SeasonDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<SeasonModel> res = repository.findAll(spec.with(filters, SeasonFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, SeasonDTO[].class));
    }

    /**
     * Method that get a single random season from the database
     *
     * @see SeasonController#findRandom(String)
     * @since 1.0.0
     */
    public SeasonDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<SeasonModel> page = repository.findAll(spec.withTranslation(language), singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), SeasonDTO.class);
    }

    /**
     * Method that get all season translations from the database
     *
     * @see SeasonController#findAllTranslationsBy(UUID)
     * @since 1.0.0
     */
    public List<SeasonTranslationDTO> findAllTranslationsBy(UUID uuid) {
        List<SeasonTranslation> res = translationRepository.findAllBySeasonUuid(uuid);
        return Arrays.asList(modelMapper.map(res, SeasonTranslationDTO[].class));
    }

    /**
     * Method that get a single random season translation from the database
     *
     * @see SeasonController#findRandomTranslation(UUID)
     * @since 1.0.0
     */
    public SeasonTranslationDTO findRandomTranslation(UUID uuid) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<SeasonTranslation> page = translationRepository.findAllBySeasonUuid(uuid, singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), SeasonTranslationDTO.class);
    }

    /**
     * Method that get a single season from the database
     *
     * @see SeasonController#findBy(UUID, String)
     * @since 1.0.0
     */
    public SeasonDTO findBy(UUID uuid, String language) {
        SeasonModel res = repository
                .findByUuidAndTranslationLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, SeasonDTO.class);
    }

    /**
     * Method that get a single season translation from the database
     *
     * @see SeasonController#findTranslationBy(UUID, String)
     * @since 1.0.0
     */
    public SeasonTranslationDTO findTranslationBy(UUID uuid, String language) {
        SeasonTranslation res = translationRepository
                .findBySeasonUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, SeasonTranslationDTO.class);
    }

    /**
     * Method that crates a season on the database
     *
     * @see SeasonController#save(SeasonDTO)
     * @since 1.0.0
     */
    public SeasonDTO save(SeasonDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);
        dto.setLanguages(Collections.singletonList(dto.getLanguage()));

        validateDTO(uuid, dto.getLanguage());

        SeasonModel dtoToModel = modelMapper.map(dto, SeasonModel.class);
        dtoToModel.getTranslation().setSeasonUuid(uuid);

        SeasonModel res = repository.save(dtoToModel);

        return modelMapper.map(res, SeasonDTO.class);
    }

    /**
     * Method that crates a season translation on the database
     *
     * @see SeasonController#saveTranslation(UUID, SeasonTranslationDTO)
     * @since 1.0.0
     */
    public SeasonTranslationDTO saveTranslation(UUID uuid, SeasonTranslationDTO dto) {
        validateDTO(uuid, dto.getLanguage());

        SeasonTranslation dtoToModel = modelMapper.map(dto, SeasonTranslation.class);
        dtoToModel.setSeasonUuid(uuid);

        SeasonTranslation res = translationRepository.save(dtoToModel);

        return modelMapper.map(res, SeasonTranslationDTO.class);
    }

    /**
     * Method that updates a season on the database
     *
     * @see SeasonController#patch(UUID, SeasonDTO)
     * @since 1.0.0
     */
    public void patch(UUID uuid, SeasonDTO patch) throws IOException {
        SeasonModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        SeasonModel dtoToModel = modelMapper.map(dbRes, SeasonModel.class);
        SeasonModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    /**
     * Method that updates a season translation on the database
     *
     * @see SeasonController#patchTranslation(UUID, String, SeasonTranslationDTO)
     * @since 1.0.0
     */
    public void patchTranslation(UUID uuid, String language, SeasonTranslationDTO patch) throws IOException {
        SeasonTranslation translation = translationRepository.findBySeasonUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);

        SeasonTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setSeasonUuid(uuid);

        translationRepository.save(patchedTranslation);
    }

    /**
     * Method that delete a season from the database
     *
     * @see SeasonController#delete(UUID)
     * @since 1.0.0
     */
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    /**
     * Method that delete a season translation from the database
     *
     * @see SeasonController#deleteTranslation(UUID, String)
     * @since 1.0.0
     */
    public void deleteTranslation(UUID uuid, String language) {
        if (!translationRepository.existsBySeasonUuidAndLanguage(uuid, language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteBySeasonUuidAndLanguage(uuid, language);
    }

    private void validateDTO(UUID uuid, String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            throw new BadRequestException("Column 'language' is required");
        }

        if (!languageUtils.isSupportedLanguage(language)) {
            throw new BadRequestException("Language '" + language + "' is currently not supported!");
        }

        if (translationRepository.existsBySeasonUuidAndLanguage(uuid, language)) {
            throw new SaveConflictException("Language '" + language + "' already exist!");
        }
    }
}
