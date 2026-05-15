package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Inventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventaireRepository extends JpaRepository<Inventaire, Integer> {
    Inventaire findByCode(String code);
    List<Inventaire> findByEntrepriseId(Integer entrepriseId);
    List<Inventaire> findByEntrepotId(Integer entrepotId);
    List<Inventaire> findByStatut(Inventaire.Statut statut);
}
