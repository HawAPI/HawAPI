package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.GameController;
import com.lucasjosino.hawapi.core.StringUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.filters.GameFilter;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.dto.GameDTO;
import com.lucasjosino.hawapi.models.dto.translation.GameTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.models.translations.GameTranslation;
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

import java.io.IOException;
import java.util.*;

/**
 * Methods to handle games
 *
 * @author Lucas Josino
 * @see GameController
 * @since 1.0.0
 */
@Service
public class GameServiceImpl implements GameService {

    private static final SpecificationBuilder<GameModel> spec = new SpecificationBuilder<>();

    private final Random random;

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final GameRepository repository;

    private final GameTranslationRepository translationRepository;

    @Autowired
    public GameServiceImpl(
            Random random, GameRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            GameTranslationRepository translationRepository
    ) {
        this.random = random;
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.translationRepository = translationRepository;
        this.basePath = config.getApiBaseUrl() + "/games";
    }

    /**
     * Method that get all game uuids filtering with {@link Pageable}
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
     * Method that get all games from the database
     *
     * @see GameController#findAll(Map, Pageable)
     * @since 1.0.0
     */
    public List<GameDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<GameModel> res = repository.findAll(spec.with(filters, GameFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, GameDTO[].class));
    }

    /**
     * Method that get a single random game from the database
     *
     * @see GameController#findRandom(String)
     * @since 1.0.0
     */
    public GameDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<GameModel> page = repository.findAll(spec.withTranslation(language), singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), GameDTO.class);
    }

    /**
     * Method that get all game translations from the database
     *
     * @see GameController#findAllTranslationsBy(UUID)
     * @since 1.0.0
     */
    public List<GameTranslationDTO> findAllTranslationsBy(UUID uuid) {
        existsByIdOrThrow(uuid);

        List<GameTranslation> res = translationRepository.findAllByGameUuid(uuid);
        return Arrays.asList(modelMapper.map(res, GameTranslationDTO[].class));
    }

    /**
     * Method that get a single random game translation from the database
     *
     * @see GameController#findRandomTranslation(UUID)
     * @since 1.0.0
     */
    public GameTranslationDTO findRandomTranslation(UUID uuid) {
        existsByIdOrThrow(uuid);

        long count = utils.getCountOrThrow(translationRepository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<GameTranslation> page = translationRepository.findAllByGameUuid(uuid, singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), GameTranslationDTO.class);
    }

    /**
     * Method that get a single game from the database
     *
     * @see GameController#findBy(UUID, String)
     * @since 1.0.0
     */
    public GameDTO findBy(UUID uuid, String language) {
        GameModel res = repository
                .findByUuidAndTranslationLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, GameDTO.class);
    }

    /**
     * Method that get a single game translation from the database
     *
     * @see GameController#findTranslationBy(UUID, String)
     * @since 1.0.0
     */
    public GameTranslationDTO findTranslationBy(UUID uuid, String language) {
        GameTranslation res = translationRepository
                .findByGameUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, GameTranslationDTO.class);
    }

    /**
     * Method that crates a game on the database
     *
     * @see GameController#save(GameDTO)
     * @since 1.0.0
     */
    public GameDTO save(GameDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);
        dto.setLanguages(Collections.singletonList(dto.getLanguage()));

        validateDTO(uuid, dto.getLanguage());

        GameModel dtoToModel = modelMapper.map(dto, GameModel.class);
        dtoToModel.getTranslation().setGameUuid(uuid);

        GameModel res = repository.save(dtoToModel);

        return modelMapper.map(res, GameDTO.class);
    }

    /**
     * Method that crates a game translation on the database
     *
     * @see GameController#saveTranslation(UUID, GameTranslationDTO)
     * @since 1.0.0
     */
    public GameTranslationDTO saveTranslation(UUID uuid, GameTranslationDTO dto) {
        validateDTO(uuid, dto.getLanguage());

        GameTranslation dtoToModel = modelMapper.map(dto, GameTranslation.class);
        dtoToModel.setGameUuid(uuid);

        GameTranslation res = translationRepository.save(dtoToModel);

        return modelMapper.map(res, GameTranslationDTO.class);
    }

    /**
     * Method that updates a game on the database
     *
     * @see GameController#patch(UUID, GameDTO)
     * @since 1.0.0
     */
    public void patch(UUID uuid, GameDTO patch) throws IOException {
        GameModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        GameModel dtoToModel = modelMapper.map(dbRes, GameModel.class);
        GameModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    /**
     * Method that updates a game translation on the database
     *
     * @see GameController#patchTranslation(UUID, String, GameTranslationDTO)
     * @since 1.0.0
     */
    public void patchTranslation(UUID uuid, String language, GameTranslationDTO patch) throws IOException {
        GameTranslation translation = translationRepository.findByGameUuidAndLanguage(uuid, language)
                .orElseThrow(ItemNotFoundException::new);

        GameTranslation patchedTranslation = utils.merge(translation, patch);
        patchedTranslation.setGameUuid(uuid);

        translationRepository.save(patchedTranslation);
    }

    /**
     * Method that delete a game from the database
     *
     * @see GameController#delete(UUID)
     * @since 1.0.0
     */
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    /**
     * Method that delete a game translation from the database
     *
     * @see GameController#deleteTranslation(UUID, String)
     * @since 1.0.0
     */
    public void deleteTranslation(UUID uuid, String language) {
        if (!translationRepository.existsByGameUuidAndLanguage(uuid, language)) {
            throw new ItemNotFoundException();
        }

        translationRepository.deleteByGameUuidAndLanguage(uuid, language);
    }

    /**
     * Method to validate an DTO
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
    private void validateDTO(UUID uuid, String language) {
        if (StringUtils.isNullOrEmpty(language)) {
            throw new BadRequestException("Column 'language' is required");
        }

        if (translationRepository.existsByGameUuidAndLanguage(uuid, language)) {
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
        throw new ItemNotFoundException("Game '" + uuid + "' not found!");
    }
}
