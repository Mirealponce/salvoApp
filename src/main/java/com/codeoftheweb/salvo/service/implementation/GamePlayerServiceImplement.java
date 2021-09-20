package com.codeoftheweb.salvo.service.implementation;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.service.GamePlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GamePlayerServiceImplement implements GamePlayerService {
    @Autowired
    GamePlayerRepository gamePlayerRepository;


    @Override
    public GamePlayer saveGamePlayer(GamePlayer gp) {
        return gamePlayerRepository.save(gp);
    }

    @Override
    public List<GamePlayer> getGamePlayer() {
        return gamePlayerRepository.findAll();
    }

    @Override
    public GamePlayer updateGamePlayer(GamePlayer gp) {
        return null;
    }

    @Override
    public boolean deleteGamePlayer(Long id) {
        return false;
    }

    @Override
    public GamePlayer findGamePlayerById(Long id) {
        return gamePlayerRepository.findById(id).orElse(null);
    }
}
