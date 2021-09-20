package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private long id;

    private int turn;

    //muchos a uno
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameplayerid")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private GamePlayer gamePlayerID;

    @ElementCollection
    @Column(name = "salvolocation")
    private List<String> salvoLocations;

    public Salvo(){

    }
    public Salvo(int turn,GamePlayer gp,List<String>salvoLocations){
        this.id=id;
        this.turn=turn;
        this.gamePlayerID=gp;
        this.salvoLocations=salvoLocations;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public GamePlayer getGamePlayerID() {
        return gamePlayerID;
    }

    public void setGamePlayerID(GamePlayer gamePlayerID) {
        this.gamePlayerID = gamePlayerID;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> getInfoSalvo(){
        Map<String,Object> dto= new LinkedHashMap<String,Object>();

        dto.put("turn",this.getTurn());
        dto.put("player",this.getGamePlayerID().getPlayer().getId());
        dto.put("locations",this.getSalvoLocations());
        return dto;
    }
}
