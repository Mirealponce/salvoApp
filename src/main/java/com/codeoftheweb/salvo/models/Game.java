package com.codeoftheweb.salvo.models;


import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import static java.util.stream.Collectors.toList;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date Created;

    //relación 1:n con tabla GamePlayer
    @OneToMany(mappedBy="GameId", fetch=FetchType.EAGER)
    public Set<GamePlayer> listgameplayer;

    //relación 1:n con tabla Score
    @OneToMany(mappedBy="gameID", fetch=FetchType.EAGER)
    Set<Score> scores;

    public Game() {
        this.Created= new Date();
    }
/*
    public Game(Date created) {
        Created = created;

    }

 */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreated() {
        return Created.toString();
    }

    public void setCreated(Date created) {
        this.Created = created;
    }

    @JsonIgnore
    public List<Player> GetPlayers(){
        return listgameplayer.stream().map(GamePlayer::getPlayer).collect(toList());
    }

    public List<GamePlayer> getGamePlayers(){
        return new ArrayList<>(this.listgameplayer);
    }

    public Set<Score> getScores() {
        return scores;
    }






}
