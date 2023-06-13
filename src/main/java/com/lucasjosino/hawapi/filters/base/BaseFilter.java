package com.lucasjosino.hawapi.filters.base;

import java.time.LocalDateTime;

/**
 * A base filter model common fields.
 *
 * @author Lucas Josino
 * @since 1.0.0
 */
abstract public class BaseFilter {

    private LocalDateTime updatedAt;

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}