package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameplayerid")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private GamePlayer gamePlayerID;

    @ElementCollection
    @Column(name = "shiplocation")
    private List<String> shipLocations;

     public Ship(){}
     public Ship(String type, GamePlayer gameplayer, List<String> location){
         this.id=id;
         this.type=type;
         this.gamePlayerID=gameplayer;
         this.shipLocations=location;

     }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GamePlayer getGamePlayerID() {
        return gamePlayerID;
    }

    public void setGamePlayerID(GamePlayer gamePlayerID) {
        this.gamePlayerID = gamePlayerID;
    }

    public List<String> getshipLocations() {
        return shipLocations;
    }

    public void setListshipLocation(List<String> listshipLocation) {
        this.shipLocations = listshipLocation;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", gamePlayerID=" + gamePlayerID +
                ", shipLocations=" + shipLocations +
                '}';
    }
    @JsonIgnore
    public Map<String,Object>getShipinfo(){
         Map<String,Object> dato= new LinkedHashMap<String, Object>();

         dato.put("type",getType());
         dato.put("locations",getshipLocations());
         return dato;
    }

}
