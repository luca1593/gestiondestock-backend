package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.CommandeFournisseur;
import com.devtech.gestiondestock.model.Fournisseur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Integer> {
    Optional<CommandeFournisseur> findCommandeFournisseurByCode(String code);
    List<CommandeFournisseur> findCommandeFournisseurByDateCommande(Instant dateCommade);
    List<CommandeFournisseur> findAllByFournisseur(Fournisseur fournisseur);

    @Query("SELECT cf FROM CommandeFournisseur cf WHERE cf.identreprise = :identreprise")
    List<CommandeFournisseur> findAllByIdentreprise(@Param("identreprise") Integer identreprise);
}
