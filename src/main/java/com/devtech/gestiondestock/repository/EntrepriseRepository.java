package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {

    Optional<Entreprise> findEntrepriseByNom(String nom);
    Optional<Entreprise> findEntrepriseByEmail(String email);
}
