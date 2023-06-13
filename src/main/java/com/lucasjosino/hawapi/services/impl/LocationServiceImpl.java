package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.LocationController;
import com.lucasjosino.hawapi.core.LanguageUtils;
import com.lucasjosino.hawapi.core.StringUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.LocationFilter;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.models.dto.LocationDTO;
import com.lucasjosino.hawapi.models.dto.translation.LocationTranslationDTO;
import com.lucasjosino.hawapi.models.translations.LocationTranslation;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.repositories.translation.LocationTranslationRepository;
import com.lucasjosino.hawapi.services.LocationService;
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
 * Methods to handle locations
 *
 * @author Lucas Josino
 * @see LocationController
 * @since 1.0.0
 */
@Service
public class LocationServiceImpl implements LocationService {

    private static final SpecificationBuilder<LocationModel> spec = new SpecificationBuilder<>();

    private static final Random random = new Random();

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final LanguageUtils languageUtils;

    private final LocationRepository repository;

    private final LocationTranslationRepository translationRepository;

    @Autowired
    public LocationServiceImpl(
            LocationRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            LanguageUtils languageUtils, LocationTranslationRepository translationRepository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.languageUtils = languageUtils;
        this.translationRepository = translationRepository;
        this.basePath = config.getApiBaseUrl() + "/places";
    }

    /**
     * Method that get all location uuids filtering with {@link Pageable}
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
     * Method that get all locations from the database
     *
     * @see LocationController#findAll(Map, Pageable)
     * @since 1.0.0
     */
    public List<LocationDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<LocationModel> res = repository.findAll(spec.with(filters, LocationFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, LocationDTO[].class));
    }

    /**
     * Method that get a single random location from the database
     *
     * @see LocationController#findRandom(String)
     * @since 1.0.0
     */
    public LocationDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<LocationModel> page = repository.findAll(spec.withTranslation(language), singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), LocationDTO.class);
    }

    /**
     * Method that get all location translations from the database
     *
     * @see LocationController#findAllTranslationsBy(UUID)
     * @since 1.0.0
     */
    public List<LocationTranslationDTO> findAllTranslationsBy(UUID uuid) {
        List<LocationTranslation> res = translationRepository.findAllByLocationUuid(uuid);
        return Arrays.asList(modelMapper.map(res, LocationTranslationDTO[].class));
    }

    /**
     * Method that get a single random location translation from the database
     *
     * @see LocationController#findRandomTranslation(UUID)
     * @since 1.0.0
     */
    public LocationTranslationDTO findRandomTranslation(UUID uuid) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<LocationTranslation> page = translationRepository.findAllByLocationUuid(uuid, singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), LocationTranslationDTO.class);
    }

    /**
     * Method that get a single location from the database
     *
     * @see LocationController#findBy(UUID, String)
     * @since 1.0.0
     */
    public LocationDTO findBy(UUID uuid, String language) {
        LocationModel res = repository
                .findByUuidAndTranslationLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, LocationDTO.class);
    }

    /**
     * Method that get a single location translation from the database
     *
     * @see LocationController#findTranslationBy(UUID, String)
     * @since 1.0.0
     */
    public LocationTranslationDTO findTranslationBy(UUID uuid, String language) {
        LocationTranslation res = translationRepository
                .findByLocationUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, LocationTranslationDTO.class);
    }

    /**
     * Method that crates a location on the database
     *
     * @see LocationController#save(LocationDTO)
     * @since 1.0.0
     */
    public LocationDTO save(LocationDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);
        dto.setLanguages(Collections.singletonList(dto.getLanguage()));

        validateDTO(uuid, dto.getLanguage());

        LocationModel dtoToModel = modelMapper.map(dto, LocationModel.class);
        dtoToModel.getTranslation().setLocationUuid(uuid);

        LocationModel res = repository.save(dtoToModel);

        return modelMapper.map(res, LocationDTO.class);
    }

    /**
     * Method that crates a location translation on the database
     *
     * @see LocationController#saveTranslation(UUID, LocationTranslationDTO)
     * @since 1.0.0
     */
    public LocationTranslationDTO saveTranslation(UUID uuid, LocationTranslationDTO dto) {
        validateDTO(uuid, dto.getLanguage());

        LocationTranslation dtoToModel = modelMapper.map(dto, LocationTranslation.class);
        dtoToModel.setLocationUuid(uuid);

        LocationTranslation res = translationRepository.save(dtoToModel);

        return modelMapper.map(res, LocationTranslationDTO.class);
    }

    /**
     * Method that updates a location on the database
     *
     * @see LocationController#patch(UUID, LocationDTO)
     * @since 1.0.0
     */
    public void patch(UUID uuid, LocationDTO patch) throws IOException {
        LocationModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        LocationModel dtoToModel = modelMapper.map(dbRes, LocationModel.class);
        LocationModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    /**
     * Method that updates a location translation on the database
     *
     * @see LocationController#patchTranslation(UUID, String, LocationTranslationDTO)
     * @since 1.0.0
     */
    public void patchTranslation(UUID uuid, String language, LocationTranslationDTO patch) throws IOException {
        LocationTranslation translation = translationRepository.findByLocationUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);

        LocationTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setLocationUuid(uuid);

        translationRepository.save(patchedTranslation);
    }

    /**
     * Method that delete a location from the database
     *
     * @see LocationController#delete(UUID)
     * @since 1.0.0
     */
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    /**
     * Method that delete a location translation from the database
     *
     * @see LocationController#deleteTranslation(UUID, String)
     * @since 1.0.0
     */
    public void deleteTranslation(UUID uuid, String language) {
        if (!translationRepository.existsByLocationUuidAndLanguage(uuid, language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteByLocationUuidAndLanguage(uuid, language);
    }

    private void validateDTO(UUID uuid, String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            throw new BadRequestException("Column 'language' is required");
        }

        if (!languageUtils.isSupportedLanguage(language)) {
            throw new BadRequestException("Language '" + language + "' is currently not supported!");
        }

        if (translationRepository.existsByLocationUuidAndLanguage(uuid, language)) {
            throw new SaveConflictException("Language '" + language + "' already exist!");
        }
    }
}
