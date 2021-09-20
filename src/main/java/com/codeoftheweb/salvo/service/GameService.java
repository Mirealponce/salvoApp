package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.models.Game;

import java.util.List;

public interface GameService {
     Game saveGame(Game game);
     List<Game> getGames();
     Game updateGame(Game game);
     boolean deleteGame(Long id);
     Game findGameById(Long id);


}
