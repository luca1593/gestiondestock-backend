package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.TransfertStockApi;
import com.devtech.gestiondestock.dto.TransfertStockDto;
import com.devtech.gestiondestock.model.TransfertStock;
import com.devtech.gestiondestock.services.TransfertStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TransfertStockController implements TransfertStockApi {
    private final TransfertStockService transfertStockService;

    @Autowired
    public TransfertStockController(TransfertStockService transfertStockService) {
        this.transfertStockService = transfertStockService;
    }

    @Override
    public TransfertStockDto save(TransfertStockDto dto) {
        return transfertStockService.save(dto);
    }

    @Override
    public TransfertStockDto findById(Integer id) {
        return transfertStockService.findById(id);
    }

    @Override
    public List<TransfertStockDto> findAll() {
        return transfertStockService.findAll();
    }

    @Override
    public List<TransfertStockDto> findByEntreprise(Integer entrepriseId) {
        return transfertStockService.findByEntreprise(entrepriseId);
    }

    @Override
    public List<TransfertStockDto> findByStatut(TransfertStock.StatutTransfert statut) {
        return transfertStockService.findByStatut(statut);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public void delete(Integer id) {
        transfertStockService.delete(id);
    }
}
