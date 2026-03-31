package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Avoir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AvoirRepository extends JpaRepository<Avoir, Integer> {
    List<Avoir> findByClientIdAndIdentreprise(Integer clientId, Integer identreprise);
    List<Avoir> findByVenteIdAndIdentreprise(Integer venteId, Integer identreprise);
    List<Avoir> findByEtatAndIdentreprise(String etat, Integer identreprise);

    @Query("SELECT a FROM Avoir a WHERE a.dateAvoir BETWEEN :debut AND :fin AND a.identreprise = :identreprise")
    List<Avoir> findByDateRange(@Param("debut") Instant debut, @Param("fin") Instant fin, @Param("identreprise") Integer identreprise);

    @Query("SELECT a FROM Avoir a WHERE a.code LIKE %:code% AND a.identreprise = :identreprise")
    List<Avoir> findByCode(@Param("code") String code, @Param("identreprise") Integer identreprise);
}
