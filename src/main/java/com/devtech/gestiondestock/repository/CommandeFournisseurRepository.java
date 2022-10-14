package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.CommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Integer> {

    Optional<CommandeFournisseur> findCommandeFournisseurByCode(String code);
    List<CommandeFournisseur> findCommandeFournisseurByDateCommande(Instant dateCommade);
}
