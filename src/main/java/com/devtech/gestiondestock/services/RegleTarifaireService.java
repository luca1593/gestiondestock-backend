package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.RegleTarifaireDto;
import com.devtech.gestiondestock.model.RegleTarifaire;
import java.util.List;

public interface RegleTarifaireService {
    RegleTarifaireDto save(RegleTarifaireDto dto);
    RegleTarifaireDto findById(Integer id);
    RegleTarifaireDto findByCode(String code);
    List<RegleTarifaireDto> findAll();
    List<RegleTarifaireDto> findByEntreprise(Integer entrepriseId);
    List<RegleTarifaireDto> findByCategorie(Integer categorieId);
    List<RegleTarifaireDto> findByTypeRegle(RegleTarifaire.TypeRegle typeRegle);
    List<RegleTarifaireDto> findActifs();
    void delete(Integer id);
}
