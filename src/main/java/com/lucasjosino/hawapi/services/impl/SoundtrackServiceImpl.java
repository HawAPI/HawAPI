package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.SoundtrackFilter;
import com.lucasjosino.hawapi.models.SoundtrackModel;
import com.lucasjosino.hawapi.models.dto.SoundtrackDTO;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
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

@Service
public class SoundtrackServiceImpl implements SoundtrackService {

    private static final SpecificationBuilder<SoundtrackModel> spec = new SpecificationBuilder<>();

    private static final Random random = new Random();

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final SoundtrackRepository repository;

    @Autowired
    public SoundtrackServiceImpl(
            SoundtrackRepository repository,
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.basePath = config.getApiBaseUrl() + "/soundtracks";
    }

    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    public List<SoundtrackDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<SoundtrackModel> res = repository.findAll(spec.with(filters, SoundtrackFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, SoundtrackDTO[].class));
    }

    public SoundtrackDTO findRandom(String language) {
        long count = repository.count();
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<SoundtrackModel> page = repository.findAll(singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), SoundtrackDTO.class);
    }

    public SoundtrackDTO findBy(UUID uuid, String language) {
        SoundtrackModel res = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, SoundtrackDTO.class);
    }

    public SoundtrackDTO save(SoundtrackDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        SoundtrackModel dtoToModel = modelMapper.map(dto, SoundtrackModel.class);
        SoundtrackModel res = repository.save(dtoToModel);

        return modelMapper.map(res, SoundtrackDTO.class);
    }

    public void patch(UUID uuid, SoundtrackDTO patch) throws IOException {
        SoundtrackModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        SoundtrackModel dtoToModel = modelMapper.map(dbRes, SoundtrackModel.class);
        SoundtrackModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }
}
