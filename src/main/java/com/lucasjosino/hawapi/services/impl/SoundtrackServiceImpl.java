package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.SoundtrackController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SoundtrackFilter;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.SoundtrackRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.SoundtrackService;
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
 * Methods to handle soundtracks
 *
 * @author Lucas Josino
 * @see SoundtrackController
 * @since 1.0.0
 */
@Service
public class SoundtrackServiceImpl implements SoundtrackService {

    private static final SpecificationBuilder<SoundtrackModel> spec = new SpecificationBuilder<>();

    private final Random random;

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final SoundtrackRepository repository;

    @Autowired
    public SoundtrackServiceImpl(
            Random random, SoundtrackRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper
    ) {
        this.random = random;
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.basePath = config.getApiBaseUrl() + "/soundtracks";
    }

    /**
     * Method that the count of all soundtracks
     *
     * @return The count of all soundtracks
     * @since 1.0.0
     */
    public long getCount() {
        return repository.count();
    }

    /**
     * Method that get all soundtrack uuids filtering with {@link Pageable}
     *
     * @param pageable An {@link Page} with pageable params. Can be null
     * @return A {@link Page} of {@link UUID} or empty
     * @since 1.0.0
     */
    public Page<UUID> findAllUUIDs(Pageable pageable, long count) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    /**
     * Method that get all soundtracks from the database
     *
     * @see SoundtrackController#findAll(Map, Pageable)
     * @since 1.0.0
     */
    public List<SoundtrackDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<SoundtrackModel> res = repository.findAll(spec.with(filters, SoundtrackFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, SoundtrackDTO[].class));
    }

    /**
     * Method that get a single random soundtrack from the database
     *
     * @see SoundtrackController#findRandom(String)
     * @since 1.0.0
     */
    public SoundtrackDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<SoundtrackModel> page = repository.findAll(singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), SoundtrackDTO.class);
    }

    /**
     * Method that get a single soundtrack from the database
     *
     * @see SoundtrackController#findBy(UUID, String)
     * @since 1.0.0
     */
    public SoundtrackDTO findBy(UUID uuid, String language) {
        SoundtrackModel res = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, SoundtrackDTO.class);
    }

    /**
     * Method that crates a soundtrack on the database
     *
     * @see SoundtrackController#save(SoundtrackDTO)
     * @since 1.0.0
     */
    public SoundtrackDTO save(SoundtrackDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        SoundtrackModel dtoToModel = modelMapper.map(dto, SoundtrackModel.class);
        SoundtrackModel res = repository.save(dtoToModel);

        return modelMapper.map(res, SoundtrackDTO.class);
    }

    /**
     * Method that updates a soundtrack on the database
     *
     * @see SoundtrackController#patch(UUID, SoundtrackDTO)
     * @since 1.0.0
     */
    public void patch(UUID uuid, SoundtrackDTO patch) throws IOException {
        SoundtrackModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        SoundtrackModel dtoToModel = modelMapper.map(dbRes, SoundtrackModel.class);
        SoundtrackModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    /**
     * Method that delete a soundtrack from the database
     *
     * @see SoundtrackController#delete(UUID)
     * @since 1.0.0
     */
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }
}
