package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findClientByNom(String nom);
    Optional<Client> findClientByEmail(String email);

    List<Client> findByNomContainingIgnoreCase(String nom);

    List<Client> findByEmailContainingIgnoreCase(String email);

    List<Client> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    @Query("SELECT c FROM Client c WHERE c.identreprise = :identreprise")
    List<Client> findAllByIdentreprise(@Param("identreprise") Integer identreprise);
}
