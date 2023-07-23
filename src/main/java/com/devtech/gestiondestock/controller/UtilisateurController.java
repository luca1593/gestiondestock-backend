package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.UtilisateurApi;
import com.devtech.gestiondestock.dto.ChangerMotDePasseUtilisateurDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luca
 */
@RestController
public class UtilisateurController implements UtilisateurApi {

    private UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService){
        this.utilisateurService = utilisateurService;
    }

    @Override
    public UtilisateurDto save(UtilisateurDto dto) {
        return this.utilisateurService.save(dto);
    }

    @Override
    public UtilisateurDto findById(Integer id) {
        return this.utilisateurService.findById(id);
    }

    @Override
    public UtilisateurDto findByNomUtilisateur(String nom) {
        return this.utilisateurService.findByNomUtilisateur(nom);
    }

    @Override
    public UtilisateurDto findByEmailUtilisateur(String email) {
        return this.utilisateurService.findByEmailUtilisateur(email);
    }

    @Override
    public List<UtilisateurDto> findAll() {
        return this.utilisateurService.findAll();
    }

    @Override
    public UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto) {
        return this.utilisateurService.changerMotDePasse(dto);
    }

    @Override
    public void delete(Integer id) {
        this.utilisateurService.delete(id);
    }

}
