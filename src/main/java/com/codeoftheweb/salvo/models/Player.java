package com.codeoftheweb.salvo.models;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private long id;
    private String userName;

    private String password;

    //relación 1:n con tabla Score
    @OneToMany(mappedBy="PlayerId", fetch=FetchType.EAGER)
    Set<GamePlayer> listgameplayer;

    //relación 1:n con tabla Score
    @OneToMany(mappedBy="playerID", fetch=FetchType.EAGER)
    Set<Score> scores;



    public Set<Score> getScores() {
        return scores;
    }
    public Score getScore(Game game){
         return this.getScores().stream().filter(s->s.getGameID().equals(game)).findFirst().get();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player() { }

    public Player(String username, String password) {
        this.userName = username;
        this.password=password;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Game> GetGames(){
        return listgameplayer.stream().map(gp -> gp.getGame()).collect(Collectors.toList());
    }

    public Map<String, Object> getInfo() {
        Map<String, Object> dato = new LinkedHashMap<String, Object>();
        dato.put("id", this.getId());
        dato.put("email",this.getUserName());

        return dato;
    }








}

