package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.TransfertStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransfertStockRepository extends JpaRepository<TransfertStock, Integer> {
    TransfertStock findByCode(String code);
    List<TransfertStock> findByEntrepriseId(Integer entrepriseId);
    List<TransfertStock> findByStatut(TransfertStock.StatutTransfert statut);
    List<TransfertStock> findByEntrepotSourceId(Integer entrepotId);
    List<TransfertStock> findByEntrepotDestinationId(Integer entrepotId);
}
