package com.lucasjosino.hawapi.services;

import com.lucasjosino.hawapi.configs.OpenAPIConfig;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final String basePath;

    @Autowired
    public GameService(GameRepository gameRepository, OpenAPIConfig config) {
        this.gameRepository = gameRepository;
        this.basePath = config.getApiBaseUrl() + "/games";
    }

    @Transactional
    public List<GameModel> findAll() {
        return gameRepository.findAll();
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
    public void delete(UUID uuid) {
        gameRepository.deleteById(uuid);
    }
}
