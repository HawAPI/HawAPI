package com.lucasjosino.hawapi.filters;

import com.lucasjosino.hawapi.controllers.api.v1.LocationController;
import com.lucasjosino.hawapi.filters.base.BaseTranslationFilter;
import com.lucasjosino.hawapi.models.LocationModel;
import com.lucasjosino.hawapi.models.dto.LocationDTO;
import com.lucasjosino.hawapi.repositories.LocationRepository;
import com.lucasjosino.hawapi.repositories.specification.SpecificationBuilder;
import com.lucasjosino.hawapi.services.LocationService;

/**
 * Location filter model
 *
 * @author Lucas Josino
 * @see LocationModel
 * @see LocationDTO
 * @see LocationController
 * @see LocationService
 * @see LocationRepository
 * @see SpecificationBuilder
 * @since 1.0.0
 */
public class LocationFilter extends BaseTranslationFilter {

    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}