package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.AvoirDto;

import java.time.Instant;
import java.util.List;

public interface AvoirService {
    AvoirDto save(AvoirDto dto);
    AvoirDto findById(Integer id);
    List<AvoirDto> findByClientId(Integer clientId, Integer identreprise);
    List<AvoirDto> findByVenteId(Integer venteId, Integer identreprise);
    List<AvoirDto> findByEtat(String etat, Integer identreprise);
    List<AvoirDto> findByDateRange(Instant debut, Instant fin, Integer identreprise);
    List<AvoirDto> findAll(Integer identreprise);
    void delete(Integer id);
}
