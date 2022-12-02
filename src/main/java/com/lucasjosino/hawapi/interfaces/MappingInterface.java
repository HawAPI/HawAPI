package com.lucasjosino.hawapi.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface MappingInterface<T> {
    ResponseEntity<List<T>> findAll();

    ResponseEntity<T> findByUUID(UUID uuid);

    ResponseEntity<T> save(T model);

    ResponseEntity<Void> delete(UUID uuid);
}
