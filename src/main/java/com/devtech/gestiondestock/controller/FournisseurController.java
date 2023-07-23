package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.FournisseurApi;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.services.FournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luca
 */
@RestController
public class FournisseurController implements FournisseurApi {

    private FournisseurService fournisseurService;

    @Autowired
    public FournisseurController(FournisseurService fournisseurService){
        this.fournisseurService = fournisseurService;
    }

    @Override
    public FournisseurDto save(FournisseurDto dto) {
        return this.fournisseurService.save(dto);
    }

    @Override
    public FournisseurDto findById(Integer id) {
        return this.fournisseurService.findById(id);
    }

    @Override
    public FournisseurDto findByNomFournisseur(String nom) {
        return this.fournisseurService.findByNomFournisseur(nom);
    }

    @Override
    public FournisseurDto findByEmailFournisseur(String email) {
        return this.fournisseurService.findByEmailFournisseur(email);
    }

    @Override
    public List<FournisseurDto> findAll() {
        return this.fournisseurService.findAll();
    }

    @Override
    public void delete(Integer id) {
        this.fournisseurService.delete(id);
    }
}
