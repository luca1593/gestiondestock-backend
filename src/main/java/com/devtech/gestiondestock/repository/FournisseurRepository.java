package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {

    Optional<Fournisseur> findFournisseurByNom(String nom);

    Optional<Fournisseur> findFournisseurByEmail(String email);

}
