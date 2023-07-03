package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.ActorController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.models.ActorSocialModel;
import com.lucasjosino.hawapi.models.dto.ActorDTO;
import com.lucasjosino.hawapi.models.dto.ActorSocialDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import com.lucasjosino.hawapi.repositories.ActorSocialRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.ActorService;
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
 * Methods to handle actors
 *
 * @author Lucas Josino
 * @see ActorController
 * @since 1.0.0
 */
@Service
public class ActorServiceImpl implements ActorService {

    private static final SpecificationBuilder<ActorModel> spec = new SpecificationBuilder<>();

    private final Random random;

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final ActorRepository repository;

    private final ActorSocialRepository socialRepository;

    @Autowired
    public ActorServiceImpl(
            Random random, ActorRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            ActorSocialRepository socialRepository
    ) {
        this.random = random;
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.basePath = config.getApiBaseUrl() + "/actors";
        this.socialRepository = socialRepository;
    }

    /**
     * Method that get all actor uuids filtering with {@link Pageable}
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
     * Method that get all actors from the database
     *
     * @see ActorController#findAll(Map, Pageable)
     * @since 1.0.0
     */
    public List<ActorDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<ActorModel> res = repository.findAll(spec.with(filters, ActorFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, ActorDTO[].class));
    }

    /**
     * Method that get all (actor) socials from the database
     *
     * @see ActorController#findAllSocials(UUID)
     * @since 1.0.0
     */
    public List<ActorSocialDTO> findAllSocials(UUID uuid) {
        existsByIdOrThrow(uuid);

        List<ActorSocialModel> res = socialRepository.findAll();
        return Arrays.asList(modelMapper.map(res, ActorSocialDTO[].class));
    }

    /**
     * Method that get a single random actor from the database
     *
     * @see ActorController#findRandom(String)
     * @since 1.0.0
     */
    public ActorDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<ActorModel> page = repository.findAll(singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), ActorDTO.class);
    }

    /**
     * Method that get a single random (actor) social from the database
     *
     * @see ActorController#findRandomSocial(UUID)
     * @since 1.0.0
     */
    public ActorSocialDTO findRandomSocial(UUID uuid) {
        existsByIdOrThrow(uuid);

        long count = utils.getCountOrThrow(socialRepository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<ActorSocialModel> page = socialRepository.findAll(singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), ActorSocialDTO.class);
    }

    /**
     * Method that get a single actor from the database
     *
     * @see ActorController#findBy(UUID, String)
     * @since 1.0.0
     */
    public ActorDTO findBy(UUID uuid, String language) {
        ActorModel res = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, ActorDTO.class);
    }

    /**
     * Method that get a single (actor) social from the database
     *
     * @see ActorController#findSocialBy(UUID, String)
     * @since 1.0.0
     */
    public ActorSocialDTO findSocialBy(UUID uuid, String name) {
        ActorSocialModel res = socialRepository.findByActorUuidAndSocial(uuid, name)
                .orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, ActorSocialDTO.class);
    }

    /**
     * Method that crates an actor on the database
     *
     * @see ActorController#save(ActorDTO)
     * @since 1.0.0
     */
    public ActorDTO save(ActorDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        if (dto.getSocials() != null && !dto.getSocials().isEmpty()) {
            dto.getSocials().forEach(actorSocial -> actorSocial.setActorUuid(uuid));
        }

        ActorModel dtoToModel = modelMapper.map(dto, ActorModel.class);
        ActorModel res = repository.save(dtoToModel);

        return modelMapper.map(res, ActorDTO.class);
    }

    /**
     * Method that crates an (actor) social on the database
     *
     * @see ActorController#saveSocial(UUID, ActorSocialDTO)
     * @since 1.0.0
     */
    public ActorSocialDTO saveSocial(UUID uuid, ActorSocialDTO dto) {
        if (!repository.existsById(uuid)) {
            throw new ItemNotFoundException("Item '" + uuid + "' doesn't exist!");
        }

        ActorSocialModel dtoToModel = modelMapper.map(dto, ActorSocialModel.class);
        dtoToModel.setActorUuid(uuid);

        ActorSocialModel res = socialRepository.save(dtoToModel);

        return modelMapper.map(res, ActorSocialDTO.class);
    }

    /**
     * Method that updates an actor on the database
     *
     * @see ActorController#patch(UUID, ActorDTO)
     * @since 1.0.0
     */
    public void patch(UUID uuid, ActorDTO patch) throws IOException {
        ActorModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        ActorModel dtoToModel = modelMapper.map(dbRes, ActorModel.class);
        ActorModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    /**
     * Method that updates an (actor) social on the database
     *
     * @see ActorController#patchSocial(UUID, String, ActorSocialDTO)
     * @since 1.0.0
     */
    public void patchSocial(UUID uuid, String name, ActorSocialDTO patch) throws IOException {
        if (!socialRepository.existsByActorUuidAndSocial(uuid, name)) {
            throw new ItemNotFoundException("Item '" + uuid + "' and name '" + name + "'doesn't exist!");
        }

        ActorSocialModel dbRes = socialRepository.findByActorUuidAndSocial(uuid, name)
                .orElseThrow(ItemNotFoundException::new);

        ActorSocialModel dtoToModel = modelMapper.map(dbRes, ActorSocialModel.class);
        ActorSocialModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setActorUuid(uuid);
        socialRepository.save(patchedModel);
    }

    /**
     * Method that delete an actor from the database
     *
     * @see ActorController#delete(UUID)
     * @since 1.0.0
     */
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }

    /**
     * Method that delete an (actor) social from the database
     *
     * @see ActorController#deleteSocial(UUID, String)
     * @since 1.0.0
     */
    public void deleteSocial(UUID uuid, String name) {
        if (!socialRepository.existsByActorUuidAndSocial(uuid, name)) throw new ItemNotFoundException();

        socialRepository.deleteByActorUuidAndSocial(uuid, name);
    }

    /**
     * Method to validate actor
     *
     * @param uuid An {@link UUID} that represents a specific item
     * @throws ItemNotFoundException {@link UUID} doesn't exists
     */
    private void existsByIdOrThrow(UUID uuid) {
        if (repository.existsById(uuid)) return;
        throw new ItemNotFoundException("Actor '" + uuid + "' not found!");
    }
}
