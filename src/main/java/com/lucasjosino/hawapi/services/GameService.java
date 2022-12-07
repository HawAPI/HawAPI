package com.lucasjosino.hawapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.filters.GameFilter;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.repositories.GameRepository;
import com.lucasjosino.hawapi.services.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final ServiceUtils utils;

    private final String basePath;

    @Autowired
    public GameService(GameRepository gameRepository, ServiceUtils utils, OpenAPIConfig config) {
        this.gameRepository = gameRepository;
        this.utils = utils;
        this.basePath = config.getApiBaseUrl() + "/games";
    }

    @Transactional
    public List<GameModel> findAll(GameFilter filter) {
        Example<GameModel> filteredModel = utils.filter(filter, GameModel.class);
        Sort sort = utils.buildSort(filter);

        if (sort == null) return gameRepository.findAll(filteredModel);

        return gameRepository.findAll(filteredModel, sort);
    }

    @Transactional
    public GameModel findByUUID(UUID uuid) {
        Optional<GameModel> res = gameRepository.findById(uuid);

        if (res.isPresent()) return res.get();

        throw new ItemNotFoundException(GameModel.class);
    }

    @Transactional
    public GameModel save(GameModel game) {
        UUID gameUUID = UUID.randomUUID();
        game.setUuid(gameUUID);
        game.setHref(basePath + "/" + gameUUID);
        return gameRepository.save(game);
    }

    @Transactional
    public void patch(UUID uuid, JsonNode patch) throws JsonPatchException, JsonProcessingException {
        GameModel game = gameRepository.findById(uuid).orElseThrow(ItemNotFoundException::new);

        GameModel patchedGame = utils.mergePatch(game, patch, GameModel.class);

        patchedGame.setUuid(uuid);
        gameRepository.save(patchedGame);
    }

    @Transactional
    public void delete(UUID uuid) {
        gameRepository.deleteById(uuid);
    }
}
