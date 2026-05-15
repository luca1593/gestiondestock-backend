package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.RegleTarifaireApi;
import com.devtech.gestiondestock.dto.RegleTarifaireDto;
import com.devtech.gestiondestock.services.RegleTarifaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class RegleTarifaireController implements RegleTarifaireApi {
    private final RegleTarifaireService regleTarifaireService;

    @Autowired
    public RegleTarifaireController(RegleTarifaireService regleTarifaireService) {
        this.regleTarifaireService = regleTarifaireService;
    }

    @Override
    public RegleTarifaireDto save(RegleTarifaireDto dto) {
        return regleTarifaireService.save(dto);
    }

    @Override
    public RegleTarifaireDto findById(Integer id) {
        return regleTarifaireService.findById(id);
    }

    @Override
    public List<RegleTarifaireDto> findAll() {
        return regleTarifaireService.findAll();
    }

    @Override
    public List<RegleTarifaireDto> findByEntreprise(Integer entrepriseId) {
        return regleTarifaireService.findByEntreprise(entrepriseId);
    }

    @Override
    public List<RegleTarifaireDto> findByCategorie(Integer categorieId) {
        return regleTarifaireService.findByCategorie(categorieId);
    }

    @Override
    public List<RegleTarifaireDto> findActifs() {
        return regleTarifaireService.findActifs();
    }

    @Override
    public void delete(Integer id) {
        regleTarifaireService.delete(id);
    }
}
