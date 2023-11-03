package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.OverviewController;
import com.lucasjosino.hawapi.core.StringUtils;
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
import com.lucasjosino.hawapi.services.OverviewService;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Methods to handle API overview
 *
 * @author Lucas Josino
 * @see OverviewController
 * @since 1.0.0
 */
@Service
public class OverviewServiceImpl implements OverviewService {

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final OverviewRepository repository;

    private final OverviewTranslationRepository translationRepository;

    @Autowired
    public OverviewServiceImpl(
            OpenAPIProperty config,
            ServiceUtils utils,
            ModelMapper modelMapper,
            OverviewRepository repository,
            OverviewTranslationRepository translationRepository
    ) {
        this.basePath = config.getApiBaseUrl() + "/overview";
        this.utils = utils;
        this.modelMapper = modelMapper;
        this.repository = repository;
        this.translationRepository = translationRepository;
    }

    /**
     * Method that get all overview translations from the database
     *
     * @see OverviewController#findAllOverviewTranslations()
     * @since 1.0.0
     */
    public List<OverviewTranslationDTO> findAllOverviewTranslations() {
        List<OverviewTranslation> res = translationRepository.findAll();
        return Arrays.asList(modelMapper.map(res, OverviewTranslationDTO[].class));
    }

    /**
     * Method that get an overview from the database
     *
     * @see OverviewController#findOverview(String)
     * @since 1.0.0
     */
    public OverviewDTO findOverviewBy(String language) {
        OverviewModel res = repository.findByTranslationLanguage(language).orElseThrow(ItemNotFoundException::new);

        OverviewDTO dto = modelMapper.map(res, OverviewDTO.class);
        OverviewDTO.DataCountProjection dataCount = repository.getAllCounts();
        dto.setDataCount(modelMapper.map(dataCount, OverviewDTO.DataCount.class));

        return dto;
    }

    /**
     * Method that get a single overview translation from the database
     *
     * @see OverviewController#findOverviewTranslationBy(String)
     * @since 1.0.0
     */
    public OverviewTranslationDTO findOverviewTranslationBy(String language) {
        OverviewTranslation res = translationRepository.findByLanguage(language).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, OverviewTranslationDTO.class);
    }

    /**
     * Method that crates an overview on the database
     *
     * @see OverviewController#saveOverview(OverviewDTO)
     * @since 1.0.0
     */
    public OverviewDTO saveOverview(OverviewDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        validateRequest(dto.getLanguage());

        OverviewModel dtoToModel = modelMapper.map(dto, OverviewModel.class);
        dtoToModel.getTranslation().setOverviewUuid(uuid);

        OverviewModel res = repository.save(dtoToModel);

        return modelMapper.map(res, OverviewDTO.class);
    }

    /**
     * Method that crates an overview translation on the database
     *
     * @see OverviewController#saveOverviewTranslation(OverviewTranslationDTO)
     * @since 1.0.0
     */
    public OverviewTranslationDTO saveOverviewTranslation(String defaultLanguage, OverviewTranslationDTO dto) {
        String dbRes = repository.findUUID().orElseThrow(ItemNotFoundException::new);

        validateRequest(dto.getLanguage());

        OverviewTranslation dtoToModel = modelMapper.map(dto, OverviewTranslation.class);
        dtoToModel.setOverviewUuid(UUID.fromString(dbRes));

        OverviewTranslation res = translationRepository.save(dtoToModel);

        return modelMapper.map(res, OverviewTranslationDTO.class);
    }

    /**
     * Method that updates an overview on the database
     *
     * @see OverviewController#patchOverview(OverviewDTO)
     * @since 1.0.0
     */
    public void patchOverview(OverviewDTO patch) throws IOException {
        OverviewModel dbRes = repository.findByTranslationLanguage(patch.getLanguage())
                .orElseThrow(ItemNotFoundException::new);

        OverviewModel dtoToModel = modelMapper.map(dbRes, OverviewModel.class);
        OverviewModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(dbRes.getUuid());
        repository.save(patchedModel);
    }

    /**
     * Method that updates an overview translation on the database
     *
     * @see OverviewController#patchOverviewTranslation(String, OverviewTranslationDTO)
     * @since 1.0.0
     */
    public void patchOverviewTranslation(String language, OverviewTranslationDTO patch) throws IOException {
        OverviewTranslation translation = translationRepository.findByLanguage(language)
                .orElseThrow(ItemNotFoundException::new);

        OverviewTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setOverviewUuid(translation.getOverviewUuid());

        translationRepository.save(patchedTranslation);
    }

    /**
     * Method that delete an overview from the database
     *
     * @see OverviewController#deleteOverview()
     * @since 1.0.0
     */
    public void deleteOverview() {
        translationRepository.deleteAll();
        repository.deleteAll();
    }

    /**
     * Method that delete an overview translation from the database
     *
     * @see OverviewController#deleteOverviewTranslation(String)
     * @since 1.0.0
     */
    public void deleteOverviewTranslation(String language) {
        if (!translationRepository.existsByLanguage(language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteByLanguage(language);
    }

    /**
     * Method to validate an request
     * <ul>
     *     <li>Validate if language is valid</li>
     *     <li>Validate if field with same language already exists</li>
     * </ul>
     *
     * @param language An {@link String} to validate
     * @throws BadRequestException   If <strong>language</strong> field is null or empty
     * @throws SaveConflictException If item already exists
     * @since 1.0.0
     */
    private void validateRequest(String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            throw new BadRequestException("Column 'language' is required");
        }

        if (translationRepository.existsByLanguage(language)) {
            throw new SaveConflictException("Language '" + language + "' already exist!");
        }
    }
}
