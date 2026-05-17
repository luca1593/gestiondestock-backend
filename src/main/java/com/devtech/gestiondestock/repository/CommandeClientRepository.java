package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommandeClientRepository extends JpaRepository<CommandeClient, Integer> {
    Optional<CommandeClient> findCommandeClientByCode(String code);
    List<CommandeClient> findAllByDateCommande(Instant dateCommade);
    List<CommandeClient> findAllByClient(Client client);

    List<CommandeClient> findByEtatcommande(com.devtech.gestiondestock.model.EtatCommande etat);

    List<CommandeClient> findByCodeContainingIgnoreCase(String code);

    List<CommandeClient> findByDateCommandeBetween(Instant debut, Instant fin);

    @Query("SELECT cc FROM CommandeClient cc WHERE cc.identreprise = :identreprise")
    List<CommandeClient> findAllByIdentreprise(@Param("identreprise") Integer identreprise);
}
