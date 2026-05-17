package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.EntrepriseApi;
import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.services.AuthorizationService;
import com.devtech.gestiondestock.services.EntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
public class EntrepriseController implements EntrepriseApi {

    private final EntrepriseService entrepriseService;

    @Autowired
    public EntrepriseController(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @Override
    public EntrepriseDto save(@RequestBody EntrepriseDto dto) {
        return this.entrepriseService.save(dto);
    }

    @Override
    public EntrepriseDto findById(Integer id) {
        return this.entrepriseService.findById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public EntrepriseDto findByNomEntreprise(String nom) {
        return this.entrepriseService.findbyNomEntreprise(nom);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public EntrepriseDto findByEmailEntreprise(String email) {
        return this.entrepriseService.findbyEmailEntreprise(email);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public List<EntrepriseDto> findAll() {
        return this.entrepriseService.findAll();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public void delete(Integer id) {
        this.entrepriseService.delete(id);
    }
}
