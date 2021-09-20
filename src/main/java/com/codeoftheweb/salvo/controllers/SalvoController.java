package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import com.codeoftheweb.salvo.service.GamePlayerService;
import com.codeoftheweb.salvo.service.GameService;

import com.codeoftheweb.salvo.service.PlayerService;
import com.codeoftheweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class SalvoController {


    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    GamePlayerService gamePlayerService;

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        try {
            if (isGuest(authentication)) {
                dto.put("player", "Guest");
            } else {
                Player player = playerService.findByUserName(authentication.getName());

                dto.put("player", player.getInfo());
            }
            dto.put("games", gameService.getGames().stream().map(game -> makeGameDTO(game)).collect(toList()));
            return dto;
        } catch (Exception exception) {
            dto.put("ERROR EN GAMES", exception.getMessage());
            return dto;

        }

    }



    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> SingUp(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerService.findByUserName(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerService.savePlayer(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    private Util util;


    @RequestMapping("/game_view/{idGame}")
    public ResponseEntity<?> GameView(@PathVariable long idGame, Authentication authentication) {
        Player player = playerService.findByUserName(authentication.getName());

        GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(idGame);
        Game game= gameService.findGameById(gamePlayer.getGame().getId());


        try {

            if (util.isGuest(authentication)) {
                return new ResponseEntity<>(Util.makeMap("error", "No esta logeado."), HttpStatus.UNAUTHORIZED);
            }
            if (player == null) {
                return new ResponseEntity<>(Util.makeMap("error", "No existe Player."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (gamePlayer == null) {
                return new ResponseEntity<>(Util.makeMap("error", "No existe GamePlayer."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (player.getId() != gamePlayer.getPlayer().getId()) {
                return new ResponseEntity<>(Util.makeMap("error", "Este no es tu juego!"), HttpStatus.UNAUTHORIZED);
            }


            return new ResponseEntity<>(makeGameViewDTO(gamePlayer,player), HttpStatus.ACCEPTED);


        } catch (Exception exception) {
            return new ResponseEntity<>(Util.makeMap("error", exception.getMessage()+"") , HttpStatus.BAD_REQUEST);
        }
    }
    public int totalhist;

    public void setTotalhist(int totalhist){
        this.totalhist=totalhist;
    }

    public List<String> getShips(GamePlayer gp, String type){
        GamePlayer gamePlayer= gamePlayerService.findGamePlayerById(gp.getId());

        List<String>listlocationsship=gamePlayer.getShips().stream()
                .filter(ship -> ship.getType().equals(type))
                .map(ship -> ship.getshipLocations()).flatMap(List::stream).collect(toList());

        return listlocationsship;

    }



    public List<Map<String,Object>> getHits(GamePlayer self,GamePlayer oponent){
        List<Map<String,Object>> hits= new ArrayList<>();




        int carrierDamage=0;
        int battleshipDamage=0;
        int submarineDamage=0;
        int destroyerDamage=0;
        int patrolboatDamage=0;

        if(oponent.getShips()!=null&&self.getShips()!=null){
        for(Salvo salvos:oponent.getSalvos()){
            Map<String, Object>dto=new LinkedHashMap<>();
            Map<String,Object>damages= new LinkedHashMap<>();
        int carrierHits=0;
        int battleshipHits=0;
        int submarineHist=0;
        int destroyerHits=0;
        int patrolboatHits=0;

        List<String> hitLocations= new ArrayList<>();

        int missed= salvos.getSalvoLocations().size();

            for(String locationsalvo: salvos.getSalvoLocations()){
               if(this.getShips(self,"carrier").contains(locationsalvo)){
                    carrierHits++;
                    carrierDamage++;
                    hitLocations.add(locationsalvo);
                    missed--;
               }
               if(this.getShips(self,"battleship").contains(locationsalvo)){
                   battleshipHits++;
                   battleshipDamage++;
                   hitLocations.add(locationsalvo);

                   missed--;
               }
               if (this.getShips(self,"submarine").contains(locationsalvo)){
                   submarineHist++;
                   submarineDamage++;
                   hitLocations.add(locationsalvo);
                   missed--;
               }
               if (this.getShips(self,"destroyer").contains(locationsalvo)){
                    destroyerHits++;
                    destroyerDamage++;
                   hitLocations.add(locationsalvo);
                   missed--;

               }
               if (this.getShips(self,"patrolboat").contains(locationsalvo)){
                   patrolboatHits++;
                   patrolboatDamage++;
                   hitLocations.add(locationsalvo);
                   missed--;
               }

                damages.put("carrierHits",carrierHits);
                damages.put("battleshipHits",battleshipHits);
                damages.put("submarineHits",submarineHist);
                damages.put("destroyerHits",destroyerHits);
                damages.put("patrolboatHits",patrolboatHits);

            }

            damages.put("carrier",carrierDamage);
            damages.put("battleship",battleshipDamage);
            damages.put("submarine",submarineDamage);
            damages.put("destroyer",destroyerDamage);
            damages.put("patrolboat",patrolboatDamage);

            dto.put("turn",salvos.getTurn());
            dto.put("hitLocations",hitLocations);
            dto.put("damages",damages);
            dto.put("missed",missed);

            hits.add(dto);

         // int suma=  carrierDamage+battleshipDamage+submarineDamage+destroyerDamage+patrolboatDamage;
           // this.setTotalhist(suma);

        }

        }
       return hits;



    }
    public int total(int total){
        return total;
    }




    public String getStateGame(GamePlayer gamePlayer, GamePlayer oponente){
        this.SizeHits(gamePlayer,oponente);
        System.out.println("ataques que recibio oponente"+this.SizeHits(oponente,gamePlayer));
        System.out.println("ataques que recibi yo"+this.SizeHits(gamePlayer,oponente));

        //  int suma=    this.getHits(gamePlayer,oponente).stream().mapToInt(stringObjectMap ->((List<String>)stringObjectMap.get("hitLocations")).size()).sum();

        if(gamePlayer.getShips().isEmpty()){
            return "PLACESHIPS";

        }

        if (oponente.getShips().isEmpty()){
            return "WAIT";
        }
        if(oponente.getSalvos().size()<gamePlayer.getSalvos().size()){//tus salvos son mayor que el oponent

            return "WAITINGFOROPP";
        }
        if (this.SizeHits(oponente,gamePlayer)==oponente.SizeShipLocation()&&this.SizeHits(gamePlayer,oponente)==gamePlayer.SizeShipLocation()) {
            scoreRepository.save(new Score(0.5,new Date(),gamePlayer.getGame(),gamePlayer.getPlayer()));
            return "TIE";


        }
        if (this.SizeHits(oponente,gamePlayer)==oponente.SizeShipLocation()) {
            scoreRepository.save(new Score(1.0,new Date(),gamePlayer.getGame(),gamePlayer.getPlayer()));
            return "WON";

        }


        if (this.SizeHits(gamePlayer,oponente)==gamePlayer.SizeShipLocation()) {
            scoreRepository.save(new Score(0.0,new Date(),gamePlayer.getGame(),gamePlayer.getPlayer()));
            return "LOST";

        }




        return "PLAY";


    }

    public int SizeHits(GamePlayer gamePlayer,GamePlayer oponente){
        int sum=this.getHits(gamePlayer,oponente)/* Recorro objetos (dto) entregados por el método*/
            .stream()
            .mapToInt(map->((List<String>)map.get("hitLocations"))/* convierto de object a List, y
                                                                    obtengo la lista que contiene 'hitLocations'*/
            .size())/* obtengo el tamaño de las listas */
            .sum(); /* voy sumando los tamaños que se le va asisnando deacuerdo a cada salvo lanzado */

        return sum;


    }








    public Map<String,  Object> makeGameViewDTO(GamePlayer gamePlayer,Player player){
        Map<String, Object> dto = new LinkedHashMap<>();

        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self", this.getHits(gamePlayer,gamePlayer.getOponente(player,gamePlayer)) );
        hits.put("opponent",this.getHits(gamePlayer.getOponente(player,gamePlayer),gamePlayer)) ;

        dto.put("id",gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreated());
        dto.put("gameState",this.getStateGame(gamePlayer,gamePlayer.getOponente(player,gamePlayer)));
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(Collectors.toList()));
        dto.put("ships",  gamePlayer.getShips().stream().map(ship  ->  makeShipDTO(ship)).collect(Collectors.toList()));
        dto.put("salvoes",  gamePlayer.getGame().getGamePlayers().stream().flatMap(gamePlayer1 -> gamePlayer1.getSalvos().stream()
                .map(salvo -> makeSalvoDTO(salvo)))
                .collect(Collectors.toList()));
        dto.put("hits",hits);

        return dto;
    }

    public Map<String,  Object> makeSalvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", salvo.getGamePlayerID().getPlayer().getId());
        dto.put("turn", salvo.getTurn());
        dto.put("locations", salvo.getSalvoLocations());
        return  dto;
    }

    public Map<String,  Object> makeShipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getshipLocations());
        return  dto;
    }


    public Map<String,  Object> makeGameDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id",game.getId());
        dto.put("created", game.getCreated());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO(gamePlayer)).collect(Collectors.toList()));
        dto.put("scores", game.getScores().stream().map(score -> makeScoreDTO(score)).collect(Collectors.toList()));

        return dto;
    }

    public Map<String,  Object> makeScoreDTO(Score score){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("score", score.getScore());
        dto.put("dateFinish",score.getFinishDate());
        dto.put("player", score.getPlayerID().getId());
        return  dto;
    }

    public Map<String, Object> makeGamePlayerDTO(GamePlayer  gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return  dto;
    }

    public Map<String,  Object> makePlayerDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }




}




