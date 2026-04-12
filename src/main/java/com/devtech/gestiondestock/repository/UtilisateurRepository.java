package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findUtilisateurByNom(String nom);
    Optional<Utilisateur> findUtilisateurByEmail(String email);
    @Query("SELECT u FROM Utilisateur u WHERE u.entreprise.id = :idEntreprise OR u.entreprise IS NULL")
    List<Utilisateur> findAllByEntreprise(@Param("idEntreprise") Integer idEntreprise);
}
