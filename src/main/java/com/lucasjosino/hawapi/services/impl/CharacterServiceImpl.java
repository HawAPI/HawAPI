package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.CharacterController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.CharacterService;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Methods to handle characters
 *
 * @author Lucas Josino
 * @see CharacterController
 * @since 1.0.0
 */
@Service
public class CharacterServiceImpl implements CharacterService {

    private static final SpecificationBuilder<CharacterModel> spec = new SpecificationBuilder<>();

    private final Random random;

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final CharacterRepository repository;

    @Autowired
    public CharacterServiceImpl(
            Random random, ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            CharacterRepository repository
    ) {
        this.random = random;
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.basePath = config.getApiBaseUrl() + "/characters";
    }

    /**
     * Method that get all character uuids filtering with {@link Pageable}
     *
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link Page} of {@link UUID} or empty
     * @since 1.0.0
     */
    public Page<UUID> findAllUUIDs(Map<String, String> filters, Pageable pageable) {
        return repository.findAllUUIDs(pageable);
    }

    /**
     * Method that get all characters from the database
     *
     * @see CharacterController#findAll(Map, Pageable)
     * @since 1.0.0
     */
    public List<CharacterDTO> findAll(Map<String, String> filters, Page<UUID> uuids) {
        List<CharacterModel> res = repository.findAll(spec.with(filters, CharacterFilter.class, uuids.getContent()));
        return Arrays.asList(modelMapper.map(res, CharacterDTO[].class));
    }

    /**
     * Method that get a single random character from the database
     *
     * @see CharacterController#findRandom(String)
     * @since 1.0.0
     */
    public CharacterDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<CharacterModel> page = repository.findAll(singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), CharacterDTO.class);
    }

    /**
     * Method that get a single character from the database
     *
     * @see CharacterController#findBy(UUID, String)
     * @since 1.0.0
     */
    public CharacterDTO findBy(UUID uuid, String language) {
        CharacterModel res = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, CharacterDTO.class);
    }

    /**
     * Method that crates a character on the database
     *
     * @see CharacterController#save(CharacterDTO)
     * @since 1.0.0
     */
    public CharacterDTO save(CharacterDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        CharacterModel dtoToModel = modelMapper.map(dto, CharacterModel.class);
        CharacterModel res = repository.save(dtoToModel);

        return modelMapper.map(res, CharacterDTO.class);
    }

    /**
     * Method that updates a character on the database
     *
     * @see CharacterController#patch(UUID, CharacterDTO)
     * @since 1.0.0
     */
    public void patch(UUID uuid, CharacterDTO patch) throws IOException {
        CharacterModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        CharacterModel dtoToModel = modelMapper.map(dbRes, CharacterModel.class);
        CharacterModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    /**
     * Method that delete a character from the database
     *
     * @see CharacterController#delete(UUID)
     * @since 1.0.0
     */
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }
}
