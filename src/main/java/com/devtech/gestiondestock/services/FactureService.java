package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.model.Facture;
import java.util.List;

public interface FactureService {
    FactureDto save(FactureDto dto);
    FactureDto findById(Integer id);
    FactureDto findByNumeroFacture(String numeroFacture);
    List<FactureDto> findAll();
    List<FactureDto> findByClient(Integer clientId);
    List<FactureDto> findByEntreprise(Integer entrepriseId);
    List<FactureDto> findByStatut(Facture.StatutFacture statut);
    void delete(Integer id);
}
