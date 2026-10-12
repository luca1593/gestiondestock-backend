package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.CompteurFacture;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompteurFactureRepository extends JpaRepository<CompteurFacture, Integer> {

    /**
     * Recupere le compteur en verrouillant la ligne (SELECT ... FOR UPDATE) afin que
     * deux creations de facture concurrentes ne puissent pas obtenir le meme numero.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CompteurFacture c where c.identreprise = :identreprise and c.annee = :annee")
    Optional<CompteurFacture> findPourMiseAJour(@Param("identreprise") Integer identreprise,
                                                @Param("annee") Integer annee);
}
