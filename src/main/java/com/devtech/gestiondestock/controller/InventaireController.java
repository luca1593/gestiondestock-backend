package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.InventaireApi;
import com.devtech.gestiondestock.dto.InventaireDto;
import com.devtech.gestiondestock.model.Inventaire;
import com.devtech.gestiondestock.services.InventaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class InventaireController implements InventaireApi {
    private final InventaireService inventaireService;

    @Autowired
    public InventaireController(InventaireService inventaireService) {
        this.inventaireService = inventaireService;
    }

    @Override
    public InventaireDto save(InventaireDto dto) {
        return inventaireService.save(dto);
    }

    @Override
    public InventaireDto findById(Integer id) {
        return inventaireService.findById(id);
    }

    @Override
    public List<InventaireDto> findAll() {
        return inventaireService.findAll();
    }

    @Override
    public List<InventaireDto> findByEntreprise(Integer entrepriseId) {
        return inventaireService.findByEntreprise(entrepriseId);
    }

    @Override
    public List<InventaireDto> findByEntrepot(Integer entrepotId) {
        return inventaireService.findByEntrepot(entrepotId);
    }

    @Override
    public List<InventaireDto> findByStatut(Inventaire.Statut statut) {
        return inventaireService.findByStatut(statut);
    }

    @Override
    public void delete(Integer id) {
        inventaireService.delete(id);
    }
}
