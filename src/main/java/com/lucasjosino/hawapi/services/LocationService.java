package com.lucasjosino.hawapi.services;

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
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LocationService {

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final LocationRepository repository;

    private final SpecificationBuilder<LocationModel> spec;

    private final LocationTranslationRepository translationRepository;

    @Autowired
    public LocationService(
            LocationRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            LocationTranslationRepository translationRepository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.spec = new SpecificationBuilder<>();
        this.translationRepository = translationRepository;
        this.basePath = config.getApiBaseUrl() + "/places";
    }

    @Transactional
    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    @Transactional
    public List<LocationDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<LocationModel> res = repository.findAll(spec.with(filters, LocationFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, LocationDTO[].class));
    }

    @Transactional
    public List<LocationTranslationDTO> findAllTranslations() {
        List<LocationTranslation> res = translationRepository.findAll();
        return Arrays.asList(modelMapper.map(res, LocationTranslationDTO[].class));
    }

    @Transactional
    public LocationDTO findBy(UUID uuid, String language) {
        LocationModel res = repository
                .findByUuidAndTranslationLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, LocationDTO.class);
    }

    @Transactional
    public LocationTranslationDTO findTranslationBy(UUID uuid, String language) {
        LocationTranslation res = translationRepository
                .findByLocationUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, LocationTranslationDTO.class);
    }

    @Transactional
    public LocationDTO save(LocationDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        LocationModel dtoToModel = modelMapper.map(dto, LocationModel.class);
        dtoToModel.getTranslation().setLocationUuid(uuid);

        LocationModel res = repository.save(dtoToModel);

        return modelMapper.map(res, LocationDTO.class);
    }

    @Transactional
    public LocationTranslationDTO saveTranslation(UUID uuid, LocationTranslation translation) {
        if (translationRepository.existsByLocationUuidAndLanguage(uuid, translation.getLanguage())) {
            throw new SaveConflictException("Language '" + translation.getLanguage() + "' already exist!");
        }

        translation.setLocationUuid(uuid);
        LocationTranslation res = translationRepository.save(translation);

        return modelMapper.map(res, LocationTranslationDTO.class);
    }

    @Transactional
    public void patch(UUID uuid, LocationDTO patch) throws IOException {
        LocationModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        LocationModel dtoToModel = modelMapper.map(dbRes, LocationModel.class);
        LocationModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    @Transactional
    public void patchTranslation(UUID uuid, String language, LocationTranslationDTO patch) throws IOException {
        LocationTranslation translation = translationRepository.findByLocationUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);

        LocationTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setLocationUuid(uuid);

        translationRepository.save(patchedTranslation);
    }

    @Transactional
    public void delete(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    @Transactional
    public void deleteTranslation(UUID uuid, String language) {
        if (!translationRepository.existsByLocationUuidAndLanguage(uuid, language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteByLocationUuidAndLanguage(uuid, language);
    }
}
