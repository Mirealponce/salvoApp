package com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private Long Id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameid")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Game GameId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinColumn(name = "player_id")
    private Player PlayerId;

    @OneToMany(mappedBy="gamePlayerID", fetch=FetchType.EAGER)
    Set<Ship> ships=new LinkedHashSet<>();

    @OrderBy
    @OneToMany(mappedBy="gamePlayerID", fetch=FetchType.EAGER)
    Set<Salvo> salvos=new LinkedHashSet<>();


    public GamePlayer(){

    }
    public GamePlayer(Game gameid,Player playerid){
    this.joinDate=new Date();
    this.GameId=gameid;
    this.PlayerId=playerid;



    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Player getPlayer() {
        return PlayerId;
    }

    public void setPlayerId(Player playerId) {
        this.PlayerId = playerId;
    }

    public Game getGame() {
        return GameId;
    }

    public Set<Ship> getShips(){

        return this.ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public void addShip(Ship newShip){
        newShip.setGamePlayerID(this);
        this.ships.add(newShip);
    }

    public void setSalvo(Salvo newSalvo){

        this.salvos.add(newSalvo);
    }

    public Set<Salvo> getSalvos() {
        return this.salvos;
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }

    public Set<Score> getScore(){
        return this.getPlayer().scores;
    }

    public GamePlayer getOponente(Player player, GamePlayer gamePlayer){
        GamePlayer Jugadores = gamePlayer.getGame().getGamePlayers().stream()
                .filter(gamePlayer1 -> gamePlayer1.getPlayer().getId()!=player.getId())
                .findFirst().orElse(null);


        if(Jugadores==null){
            Jugadores= new GamePlayer();
            return Jugadores;
        }

        return Jugadores;
    }

    public int SizeSalvoLocation(){
        int size=this.getSalvos().stream().map(salvo -> salvo.getSalvoLocations().size()).findFirst().orElse(0);

        return size;
    }
    public int SizeShipLocation(){
        int size=this.getShips().stream().mapToInt(salvo -> salvo.getshipLocations().size()).sum();

        return size;
    }





}
