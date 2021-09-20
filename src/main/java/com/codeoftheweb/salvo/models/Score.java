package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private long id;

    private double score;

    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;

    //relación con tabla Game
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameid")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Game gameID;

    //relacion con tabla Player
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="playerid")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Player playerID;

    public Score(){

    }

    public Score(double score,Date finishDate, Game game, Player player){
    this.score=score;
    this.finishDate=finishDate;
    this.gameID=game;
    this.playerID=player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Game getGameID() {
        return gameID;
    }

    public void setGameID(Game gameID) {
        this.gameID = gameID;
    }

    public Player getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Player playerID) {
        this.playerID = playerID;
    }

    public Map<String,Object> getInfoScore(){
        Map<String,Object> dto= new LinkedHashMap<String,Object>();
        dto.put("score",this.getScore());
        dto.put("player",this.playerID.getId());
        return dto;
    }




}
