package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.FournisseurApi;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.services.FournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FournisseurController implements FournisseurApi {

    private FournisseurService fournisseurService;

    @Autowired
    public FournisseurController(FournisseurService fournisseurService){
        this.fournisseurService = fournisseurService;
    }

    @Override
    public FournisseurDto save(FournisseurDto dto) {
        return fournisseurService.save(dto);
    }

    @Override
    public FournisseurDto findById(Integer id) {
        return fournisseurService.findById(id);
    }

    @Override
    public FournisseurDto findByNomFournisseur(String nom) {
        return fournisseurService.findByNomFournisseur(nom);
    }

    @Override
    public FournisseurDto findByEmailFournisseur(String email) {
        return fournisseurService.findByEmailFournisseur(email);
    }

    @Override
    public List<FournisseurDto> findAll() {
        return fournisseurService.findAll();
    }

    @Override
    public void delete(Integer id) {
        fournisseurService.delete(id);
    }
}
