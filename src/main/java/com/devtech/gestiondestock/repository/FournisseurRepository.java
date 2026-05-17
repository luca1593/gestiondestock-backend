package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {

    Optional<Fournisseur> findFournisseurByNom(String nom);
    Optional<Fournisseur> findFournisseurByEmail(String email);

    @Query("SELECT f FROM Fournisseur f WHERE f.identreprise = :identreprise")
    List<Fournisseur> findAllByIdentreprise(@Param("identreprise") Integer identreprise);
}
