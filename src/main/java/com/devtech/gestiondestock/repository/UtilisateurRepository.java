package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findUtilisateurByNom(String nom);
    Optional<Utilisateur> findUtilisateurByEmail(String email);
}
