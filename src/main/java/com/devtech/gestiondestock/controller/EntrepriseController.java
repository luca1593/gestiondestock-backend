package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.EntrepriseApi;
import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.services.EntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EntrepriseController implements EntrepriseApi {

    private EntrepriseService entrepriseService;

    @Autowired
    public EntrepriseController(EntrepriseService entrepriseService){
        this.entrepriseService = entrepriseService;
    }

    @Override
    public EntrepriseDto save(EntrepriseDto dto) {
        return entrepriseService.save(dto);
    }

    @Override
    public EntrepriseDto findById(Integer id) {
        return entrepriseService.findById(id);
    }

    @Override
    public EntrepriseDto findByNomEntreprise(String nom) {
        return entrepriseService.findbyNomEntreprise(nom);
    }

    @Override
    public EntrepriseDto findByEmailEntreprise(String email) {
        return entrepriseService.findbyEmailEntreprise(email);
    }

    @Override
    public List<EntrepriseDto> findAll() {
        return entrepriseService.findAll();
    }

    @Override
    public void delete(Integer id) {
        entrepriseService.delete(id);

    }
}
