package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Integer> {
    Facture findByNumeroFacture(String numeroFacture);
    List<Facture> findByClientId(Integer clientId);
    List<Facture> findByEntrepriseId(Integer entrepriseId);
    List<Facture> findByStatut(Facture.StatutFacture statut);
}
