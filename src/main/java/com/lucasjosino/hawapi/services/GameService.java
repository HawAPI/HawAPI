package com.lucasjosino.hawapi.services;

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
public class GameService {

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final GameRepository repository;

    private final SpecificationBuilder<GameModel> spec;

    private final GameTranslationRepository translationRepository;

    @Autowired
    public GameService(
            GameRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            GameTranslationRepository translationRepository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.spec = new SpecificationBuilder<>();
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
    public List<GameTranslationDTO> findAllTranslations() {
        List<GameTranslation> res = translationRepository.findAll();
        return Arrays.asList(modelMapper.map(res, GameTranslationDTO[].class));
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

        GameModel dtoToModel = modelMapper.map(dto, GameModel.class);
        GameModel res = repository.save(dtoToModel);

        return modelMapper.map(res, GameDTO.class);
    }

    @Transactional
    public GameTranslationDTO saveTranslation(UUID uuid, GameTranslation translation) {
        if (translationRepository.existsByGameUuidAndLanguage(uuid, translation.getLanguage())) {
            throw new SaveConflictException("Language '" + translation.getLanguage() + "' already exist!");
        }

        translation.setGameUuid(uuid);
        GameTranslation res = translationRepository.save(translation);

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
    public void delete(UUID uuid) {
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
}
