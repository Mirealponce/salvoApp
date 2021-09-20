package com.codeoftheweb.salvo.service.implementation;

import com.codeoftheweb.salvo.models.Score;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import com.codeoftheweb.salvo.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ScoreServiceImplement implements ScoreService {
    @Autowired
    private ScoreRepository scoreRepository;


    @Override
    public Score saveScore(Score score) {
        return scoreRepository.save(score);
    }

    @Override
    public List<Score> getScore() {
        return scoreRepository.findAll();
    }

    @Override
    public Score updateScore(Score score) {
        return null;
    }

    @Override
    public boolean deleteScore(Long id) {
        return false;
    }

    @Override
    public Score findScoreById(Long id) {
        return scoreRepository.findById(id).orElse(null);
    }
}
