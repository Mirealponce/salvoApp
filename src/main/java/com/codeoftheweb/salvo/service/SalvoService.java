package com.codeoftheweb.salvo.service;



import com.codeoftheweb.salvo.models.Salvo;

import java.util.List;

public interface SalvoService {
    Salvo saveSalvo(Salvo ship);
    List<Salvo> getSalvo();
    Salvo updateSalvo(Salvo salvo);
    boolean deleteSalvo(Long id);
    Salvo findSalvoById(Long id);
}
