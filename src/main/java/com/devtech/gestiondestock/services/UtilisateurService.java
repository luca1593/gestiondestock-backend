package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.ChangerMotDePasseUtilisateurDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;

import java.util.List;

public interface UtilisateurService {
    UtilisateurDto save(UtilisateurDto dto);
    UtilisateurDto findById(Integer id);
    UtilisateurDto findByIdWithPassword(Integer id);
    UtilisateurDto findByNomUtilisateur(String nom);
    UtilisateurDto findByEmailUtilisateur(String email);
    UtilisateurDto findByEmailForAuthentication(String email);
    List<UtilisateurDto> findAll();
    UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto);
    void delete(Integer id);
}
