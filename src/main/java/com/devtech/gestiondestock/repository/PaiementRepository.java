package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
    List<Paiement> findByFactureId(Integer factureId);
    List<Paiement> findByModePaiement(Paiement.ModePaiement modePaiement);
}
