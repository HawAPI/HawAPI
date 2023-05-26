package com.lucasjosino.hawapi.services.base;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BaseService<D extends BaseDTO> {

    Page<UUID> findAllUUIDs(Pageable pageable);

    List<D> findAll(Map<String, String> filters, List<UUID> uuids);

    D findRandom(String language);

    D findBy(UUID uuid, String language);

    @Transactional
    D save(D dto);

    @Transactional
    void patch(UUID uuid, D patch) throws IOException;

    @Transactional
    void deleteById(UUID uuid);
}
