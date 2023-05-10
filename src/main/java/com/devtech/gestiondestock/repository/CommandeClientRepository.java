package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommandeClientRepository extends JpaRepository<CommandeClient, Integer> {
    Optional<CommandeClient> findCommandeClientByCode(String code);
    List<CommandeClient> findAllByDateCommande(Instant dateCommade);
    List<CommandeClient> findAllByClient(Client client);
}
