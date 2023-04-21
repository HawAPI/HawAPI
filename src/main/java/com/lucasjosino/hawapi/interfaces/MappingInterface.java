package com.lucasjosino.hawapi.interfaces;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MappingInterface<D extends BaseDTO> {
    ResponseEntity<List<D>> findAll(@RequestParam Map<String, String> filters, Pageable pageable);

    ResponseEntity<D> findBy(@PathVariable UUID uuid, @RequestParam(required = false) String language);

    ResponseEntity<D> save(@RequestBody D dto);

    ResponseEntity<D> patch(@PathVariable UUID uuid, @RequestBody D dto);

    ResponseEntity<Void> delete(@PathVariable UUID uuid);
}
