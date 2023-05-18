package com.lucasjosino.hawapi.filters.base;

import java.time.LocalDateTime;

abstract public class BaseFilter {

    private LocalDateTime updatedAt;

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}