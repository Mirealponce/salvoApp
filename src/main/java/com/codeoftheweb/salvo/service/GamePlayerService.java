package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.models.GamePlayer;
import org.springframework.stereotype.Service;

import java.util.List;

public interface GamePlayerService {
    GamePlayer saveGamePlayer(GamePlayer gp);

    List<GamePlayer> getGamePlayer();

    GamePlayer updateGamePlayer(GamePlayer gp);

    boolean deleteGamePlayer(Long id);

    GamePlayer findGamePlayerById(Long id);

}
