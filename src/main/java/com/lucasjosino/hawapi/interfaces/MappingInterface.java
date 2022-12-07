package com.lucasjosino.hawapi.interfaces;

import com.lucasjosino.hawapi.filters.base.BaseFilter;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface MappingInterface<T extends BaseModel, X extends BaseFilter> {
    ResponseEntity<List<T>> findAll(X filter);

    ResponseEntity<T> findByUUID(UUID uuid);

    ResponseEntity<T> save(T model);

    ResponseEntity<Void> delete(UUID uuid);
}
