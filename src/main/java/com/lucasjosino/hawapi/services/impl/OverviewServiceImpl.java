package com.lucasjosino.hawapi.services.impl;

import com.lucasjosino.hawapi.controllers.api.v1.OverviewController;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.OverviewModel;
import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import com.lucasjosino.hawapi.repositories.OverviewRepository;
import com.lucasjosino.hawapi.services.OverviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Methods to handle API overview
 *
 * @author Lucas Josino
 * @see OverviewController
 * @since 1.0.0
 */
@Service
public class OverviewServiceImpl implements OverviewService {

    private final OverviewRepository repository;

    private final ModelMapper mapper;

    @Autowired
    public OverviewServiceImpl(OverviewRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Method that get an overview from the database
     *
     * @see OverviewController#getOverview(String)
     * @since 1.0.0
     */
    public OverviewDTO getOverviewByLanguage(String language) {
        OverviewModel res = repository.findByTranslationLanguage(language).orElseThrow(ItemNotFoundException::new);
        return mapper.map(res, OverviewDTO.class);
    }
}
