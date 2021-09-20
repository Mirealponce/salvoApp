package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import com.codeoftheweb.salvo.service.GamePlayerService;
import com.codeoftheweb.salvo.service.GameService;
import com.codeoftheweb.salvo.service.SalvoService;
import com.codeoftheweb.salvo.service.ShipService;
import com.codeoftheweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerService gamePlayerService;


    @Autowired
   private ShipService shipService;

    @Autowired
    private SalvoService salvoService;



    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createGame(Authentication authentication){

        Map<String,Object> dto= new LinkedHashMap<String, Object>();

        try{
            if (Util.isGuest(authentication)) {
                return new ResponseEntity<>(Util.makeMap("error","no esta logeado"), HttpStatus.UNAUTHORIZED);
            }
            //trae datos del player logeados
            Player player = playerRepository.findByUserName(authentication.getName());

            Game newgame= gameService.saveGame(new Game());
            GamePlayer n=gamePlayerService.saveGamePlayer(new GamePlayer(newgame,player));

            return new ResponseEntity<>(Util.makeMap("gpid", n.getId()), HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(Util.makeMap("ERROR EN CREAR GAME",exception.getMessage()),HttpStatus.FOUND);

        }

    }

    @RequestMapping(path = "/game/{gameid}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> joinGame(@PathVariable long gameid, Authentication authentication){

        if(Util.isGuest(authentication)){

            return new ResponseEntity<>(Util.makeMap("error", "No esta logeado."), HttpStatus.UNAUTHORIZED);

        }
        Player player = playerRepository.findByUserName(authentication.getName());

        Game game=gameService.findGameById(gameid);


        List<Long> players=game.getGamePlayers().stream().map(gamePlayer -> gamePlayer.getPlayer().getId()).collect(toList());

        if(players.contains(player.getId())){
            return new ResponseEntity<>(Util.makeMap("error", "No puedes jugar contra ti mismo."), HttpStatus.UNAUTHORIZED);

        }
        if(player==null){
                return new ResponseEntity<>(Util.makeMap("error", "No existe Player."), HttpStatus.UNAUTHORIZED);
            }
        if(game==null){
                return new ResponseEntity<>(Util.makeMap("error", "No existe Game."), HttpStatus.FORBIDDEN);
        }

        if(game.getGamePlayers().size()==1){

            GamePlayer gp= gamePlayerService.saveGamePlayer(new GamePlayer(game,player));

            return new ResponseEntity<>(Util.makeMap("gpid", gp.getId()), HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(Util.makeMap("error", "El juego esta lleno." ), HttpStatus.UNAUTHORIZED);

        }


    }
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createShip(Authentication authentication,
                                                         @PathVariable long gamePlayerId, @RequestBody Set<Ship> ship){
        try {
            //a este gameplayer(juego) le necesito agregar barcos
            GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(gamePlayerId);

            Player player = playerRepository.findByUserName(authentication.getName());
            if (Util.isGuest(authentication)) {
                return new ResponseEntity<>(Util.makeMap("error", "No esta logeado"), HttpStatus.UNAUTHORIZED);

            }
            if(gamePlayer==null){
                return new ResponseEntity<>(Util.makeMap("error", "No existe juego!"), HttpStatus.UNAUTHORIZED);

            }
            if(gamePlayer.getPlayer().getId()!=player.getId()){
                return new ResponseEntity<>(Util.makeMap("error", "Este no es tu juego!"), HttpStatus.UNAUTHORIZED);


            }

            //si tiene barcos guardados no puede nuevamente guardar
            if(gamePlayer.getShips().size()>0){
                return new ResponseEntity<>(Util.makeMap("error", "Ya ha colocado barcos!"), HttpStatus.FORBIDDEN);


            }
            /*
            llamo al método set de la lista de ships de gameplayer para
            actualizar la lista de barcos de ese gameplayer(player)

             */

            gamePlayer.setShips(ship);

            for(Ship newship:ship) {
                newship.setGamePlayerID(gamePlayer);
                Ship ships = shipService.saveShip(new Ship(newship.getType(), newship.getGamePlayerID(), newship.getshipLocations()));

            }
            return new ResponseEntity<>(Util.makeMap("OK", "Barcos creados!"), HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(Util.makeMap("Error al crear barcos",exception.getMessage()),HttpStatus.CONFLICT);
        }




    }
    @RequestMapping("/games/players/{gamePlayerId}/salvoes")
    @PostMapping
    public ResponseEntity<Map<String,Object>> createSalvos(Authentication authentication,
                                                           @PathVariable long gamePlayerId,
                                                           @RequestBody Salvo salvos){
        try {


            Player player = playerRepository.findByUserName(authentication.getName());
            if (Util.isGuest(authentication)) {
                return new ResponseEntity<>(Util.makeMap("error", "No esta logeado"), HttpStatus.UNAUTHORIZED);
            }

            GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(gamePlayerId);
            Long gameid = gamePlayer.getGame().getId();
            Game game = gameService.findGameById(gameid);


            GamePlayer gpoponente = gamePlayer.getOponente(player,gamePlayer);


            if(gpoponente==null){
                return new ResponseEntity<>(Util.makeMap("Error", "Aún no tienes un oponente!"), HttpStatus.UNAUTHORIZED);

            }


                if (gpoponente.getSalvos().size() < gamePlayer.getSalvos().size()) {
                    return new ResponseEntity<>(Util.makeMap("Error", "Aún no es tu turno!"), HttpStatus.UNAUTHORIZED);

                }


            if (gamePlayer == null) {
                return new ResponseEntity<>(Util.makeMap("Error", "No existe juego!"), HttpStatus.UNAUTHORIZED);
            }
            if (gamePlayer.getPlayer().getId() != player.getId()) {
                return new ResponseEntity<>(Util.makeMap("error", "Este no es tu juego!"), HttpStatus.UNAUTHORIZED);

            }

            if (salvos.getSalvoLocations().size() < 1 || salvos.getSalvoLocations().size() > 5) {
                return new ResponseEntity<>(Util.makeMap("Error", "El rango de disparos es de 1 a 5!"), HttpStatus.UNAUTHORIZED);

            }

            salvos.setGamePlayerID(gamePlayer);
            salvos.setTurn(gamePlayer.getSalvos().size() + 1);
            gamePlayer.setSalvo(salvos);
            salvoService.saveSalvo(salvos);

            return new ResponseEntity<>(Util.makeMap("OK", "salvos creados!"), HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(Util.makeMap("error", exception.getMessage()), HttpStatus.CONFLICT);

        }

    }





}

