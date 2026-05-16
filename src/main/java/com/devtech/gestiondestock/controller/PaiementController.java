package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.PaiementApi;
import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.services.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PaiementController implements PaiementApi {
    private final PaiementService paiementService;

    @Autowired
    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @Override
    public PaiementDto save(PaiementDto dto) {
        return paiementService.save(dto);
    }

    @Override
    public PaiementDto findById(Integer id) {
        return paiementService.findById(id);
    }

    @Override
    public List<PaiementDto> findAll() {
        return paiementService.findAll();
    }

    @Override
    public List<PaiementDto> findByFacture(Integer factureId) {
        return paiementService.findByFacture(factureId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public void delete(Integer id) {
        paiementService.delete(id);
    }
}
