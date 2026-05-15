package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.InventaireDto;
import com.devtech.gestiondestock.model.Inventaire;
import java.util.List;

public interface InventaireService {
    InventaireDto save(InventaireDto dto);
    InventaireDto findById(Integer id);
    InventaireDto findByCode(String code);
    List<InventaireDto> findAll();
    List<InventaireDto> findByEntreprise(Integer entrepriseId);
    List<InventaireDto> findByEntrepot(Integer entrepotId);
    List<InventaireDto> findByStatut(Inventaire.Statut statut);
    void delete(Integer id);
}
