package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.model.Facture;

import java.util.List;

public interface FactureService {
    FactureDto save(FactureDto dto);

    /**
     * Enregistre une facture et son paiement en une seule transaction.
     * Le paiement est fourni dans {@link FactureDto#getPaiement()}.
     */
    FactureDto saveAvecPaiement(FactureDto dto);

    FactureDto findById(Integer id);
    FactureDto findByNumeroFacture(String numeroFacture);
    List<FactureDto> findAll();
    List<FactureDto> findByClient(Integer clientId);
    List<FactureDto> findByFournisseur(Integer fournisseurId);
    List<FactureDto> findByEntreprise(Integer entrepriseId);
    List<FactureDto> findByStatut(Facture.StatutFacture statut);
    List<FactureDto> findByType(Facture.TypeFacture typeFacture);
    List<FactureDto> findBySource(Facture.SourceDocument sourceType, Integer sourceId);
    void delete(Integer id);
}
