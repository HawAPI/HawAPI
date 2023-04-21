package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
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
public class CharacterService {

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final CharacterRepository repository;

    private final SpecificationBuilder<CharacterModel> spec;

    @Autowired
    public CharacterService(
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            CharacterRepository repository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.spec = new SpecificationBuilder<>();
        this.basePath = config.getApiBaseUrl() + "/characters";
    }

    @Transactional
    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    @Transactional
    public List<CharacterDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<CharacterModel> res = repository.findAll(spec.with(filters, CharacterFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, CharacterDTO[].class));
    }

    @Transactional
    public CharacterDTO findBy(UUID uuid) {
        CharacterModel res = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, CharacterDTO.class);
    }

    @Transactional
    public CharacterDTO save(CharacterDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        CharacterModel dtoToModel = modelMapper.map(dto, CharacterModel.class);
        CharacterModel res = repository.save(dtoToModel);

        return modelMapper.map(res, CharacterDTO.class);
    }

    @Transactional
    public void patch(UUID uuid, CharacterDTO patch) throws IOException {
        CharacterModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        CharacterModel dtoToModel = modelMapper.map(dbRes, CharacterModel.class);
        CharacterModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    @Transactional
    public void delete(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }
}
