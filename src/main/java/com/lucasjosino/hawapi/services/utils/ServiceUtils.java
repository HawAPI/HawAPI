package com.lucasjosino.hawapi.services.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

/**
 * Service utils for HawAPI project
 *
 * @author Lucas Josino
 * @see ActorService
 * @see CharacterService
 * @see EpisodeService
 * @see GameService
 * @see LocationService
 * @see SeasonService
 * @see SoundtrackService
 * @since 1.0.0
 */
@Component
public class ServiceUtils {

    private final ObjectMapper mapper;

    @Autowired
    public ServiceUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Bean
    public Random random() {
        return new Random();
    }

    /**
     * Method to merge/update two models/dtos
     *
     * @param model The original model
     * @param dto   The dto to be merged
     * @return A merged/updated model
     * @throws IOException If {@link ObjectMapper#readerForUpdating} couldn't merge models
     * @see ObjectMapper
     */
    public <T, Y> T merge(T model, Y dto) throws IOException {
        return mapper.readerForUpdating(model).readValue((JsonNode) mapper.valueToTree(dto));
    }

    /**
     * Method to check if <strong>count</strong> is greater than 0
     *
     * @throws ItemNotFoundException If count is less or equal to 0
     */
    public long getCountOrThrow(long count) {
        if (count > 0) return count;
        throw new ItemNotFoundException("Empty list");
    }
}