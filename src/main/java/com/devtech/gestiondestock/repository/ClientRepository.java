package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findClientByNom(String nom);
    Optional<Client> findClientByEmail(String email);

    List<Client> findByNomContainingIgnoreCase(String nom);

    List<Client> findByEmailContainingIgnoreCase(String email);

    List<Client> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    List<Client> findAllByIdentreprise(Integer identreprise);
}
