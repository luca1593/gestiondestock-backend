package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.FournisseurDto;

import java.util.List;

public interface FournisseurService {

    FournisseurDto save(FournisseurDto dto);

    FournisseurDto findById(Integer id);

    FournisseurDto findByNomFournisseur(String nom);

    FournisseurDto findByEmailFournisseur(String email);

    List<FournisseurDto> findAll();

    void delete(Integer id);

}
