package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.CharacterFilter;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.models.dto.CharacterDTO;
import com.lucasjosino.hawapi.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.repositories.CharacterRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.CharacterService;
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
public class CharacterServiceImpl implements CharacterService {

    private static final SpecificationBuilder<CharacterModel> spec = new SpecificationBuilder<>();

    private static final Random random = new Random();

    private final String basePath;

    private final ServiceUtils utils;

    private final ModelMapper modelMapper;

    private final CharacterRepository repository;

    @Autowired
    public CharacterServiceImpl(
            ServiceUtils utils,
            OpenAPIProperty config,
            ModelMapper modelMapper,
            CharacterRepository repository
    ) {
        this.utils = utils;
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.basePath = config.getApiBaseUrl() + "/characters";
    }

    public Page<UUID> findAllUUIDs(Pageable pageable) {
        List<UUID> res = repository.findAllUUIDs(pageable);
        long count = repository.count();
        return PageableExecutionUtils.getPage(res, pageable, () -> count);
    }

    public List<CharacterDTO> findAll(Map<String, String> filters, List<UUID> uuids) {
        List<CharacterModel> res = repository.findAll(spec.with(filters, CharacterFilter.class, uuids));
        return Arrays.asList(modelMapper.map(res, CharacterDTO[].class));
    }

    public CharacterDTO findRandom(String language) {
        long count = utils.getCountOrThrow(repository.count());
        int index = random.nextInt((int) count);

        PageRequest singleAndRandomItem = PageRequest.of(index, 1);
        Page<CharacterModel> page = repository.findAll(singleAndRandomItem);

        return modelMapper.map(page.getContent().get(0), CharacterDTO.class);
    }

    public CharacterDTO findBy(UUID uuid, String language) {
        CharacterModel res = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);
        return modelMapper.map(res, CharacterDTO.class);
    }

    public CharacterDTO save(CharacterDTO dto) {
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setHref(basePath + "/" + uuid);

        CharacterModel dtoToModel = modelMapper.map(dto, CharacterModel.class);
        CharacterModel res = repository.save(dtoToModel);

        return modelMapper.map(res, CharacterDTO.class);
    }

    public void patch(UUID uuid, CharacterDTO patch) throws IOException {
        CharacterModel dbRes = repository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        CharacterModel dtoToModel = modelMapper.map(dbRes, CharacterModel.class);
        CharacterModel patchedModel = utils.merge(dtoToModel, patch);

        patchedModel.setUuid(uuid);
        repository.save(patchedModel);
    }

    public void deleteById(UUID uuid) {
        if (!repository.existsById(uuid)) throw new ItemNotFoundException();

        repository.deleteById(uuid);
    }
}
