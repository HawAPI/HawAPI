package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.ActorFilter;
import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.models.dto.ActorDTO;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.ActorRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
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
public class ActorService {

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final ActorRepository repository;

    private final SpecificationBuilder<ActorModel> spec;

    @Autowired
    public ActorService(ActorRepository repository, ServiceUtils utils, OpenAPIProperty config, ModelMapper modelMapper) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.spec = new SpecificationBuilder<>();
        this.basePath = config.getApiBaseUrl() + "/actors";
    }

    @Transactional
    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    @Transactional
    public List<ActorDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<ActorModel> res = repository.findAll(spec.with(filters, ActorFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, ActorDTO[].class));
    }

    @Transactional
    public ActorDTO findBy(UUID uuid) {
        ActorModel res = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, ActorDTO.class);
    }

    @Transactional
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

    @Transactional
    public void patch(UUID uuid, ActorDTO patch) throws IOException {
        ActorModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        ActorModel dtoToModel = modelMapper.map(dbRes, ActorModel.class);
        ActorModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    @Transactional
    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }
}
