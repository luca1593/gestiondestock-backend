package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.FactureApi;
import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.services.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class FactureController implements FactureApi {
    private final FactureService factureService;

    @Autowired
    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @Override
    public FactureDto save(FactureDto dto) {
        return factureService.save(dto);
    }

    @Override
    public FactureDto findById(Integer id) {
        return factureService.findById(id);
    }

    @Override
    public List<FactureDto> findAll() {
        return factureService.findAll();
    }

    @Override
    public List<FactureDto> findByClient(Integer clientId) {
        return factureService.findByClient(clientId);
    }

    @Override
    public List<FactureDto> findByEntreprise(Integer entrepriseId) {
        return factureService.findByEntreprise(entrepriseId);
    }

    @Override
    public List<FactureDto> findByStatut(Facture.StatutFacture statut) {
        return factureService.findByStatut(statut);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public void delete(Integer id) {
        factureService.delete(id);
    }
}
