package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import java.util.List;


public interface EntrepriseService {

    EntrepriseDto save(EntrepriseDto dto);

    EntrepriseDto findById(Integer id);

    EntrepriseDto findbyNomEntreprise(String nom);

    EntrepriseDto findbyEmailEntreprise(String email);

    List<EntrepriseDto> findAll();

    void delete(Integer id);

}
