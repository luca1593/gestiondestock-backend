package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.EntrepotDto;
import java.util.List;

public interface EntrepotService {
    EntrepotDto save(EntrepotDto dto);
    EntrepotDto findById(Integer id);
    List<EntrepotDto> findAll();
    List<EntrepotDto> findByEntreprise(Integer entrepriseId);
    void delete(Integer id);
}