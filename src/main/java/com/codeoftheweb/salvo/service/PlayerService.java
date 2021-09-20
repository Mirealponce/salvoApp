package com.codeoftheweb.salvo.service;


import com.codeoftheweb.salvo.models.Player;

import java.util.List;

public interface PlayerService {
    Player savePlayer(Player player);
    List<Player> getPlayer();
    Player updatePlayer(Player player);
    boolean deletePlayer(Long id);
    Player findPlayerById(Long id);
    Player findByUserName(String username);
}
