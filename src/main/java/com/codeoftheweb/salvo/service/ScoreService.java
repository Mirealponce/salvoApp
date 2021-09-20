package com.codeoftheweb.salvo.service;



import com.codeoftheweb.salvo.models.Score;

import java.util.List;

public interface ScoreService {
    Score saveScore(Score score);
    List<Score> getScore();
    Score updateScore(Score score);
    boolean deleteScore(Long id);
    Score findScoreById(Long id);
}
