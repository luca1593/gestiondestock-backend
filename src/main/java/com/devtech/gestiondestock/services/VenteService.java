package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.VenteDto;

import java.time.Instant;
import java.util.List;

public interface VenteService {

    VenteDto save(VenteDto dto);

    VenteDto findById(Integer id);

    VenteDto findByCodeVente(String code);

    List<VenteDto> findByDateVente(Instant dateVente);

    List<VenteDto> findAll();

    void delete(Integer id);

}
