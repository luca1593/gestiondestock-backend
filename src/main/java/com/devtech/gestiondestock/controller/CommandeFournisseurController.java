package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.CommandeFournisseurApi;
import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.services.CommandeFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
public class CommandeFournisseurController implements CommandeFournisseurApi {

    private CommandeFournisseurService commandeFournisseurService;

    @Autowired
    public CommandeFournisseurController(CommandeFournisseurService commandeFournisseurService) {
        this.commandeFournisseurService = commandeFournisseurService;
    }

    @Override
    public CommandeFournisseurDto save(CommandeFournisseurDto dto, Long dateCommandeFournisseur) {
        Instant dateCmd = Instant.ofEpochMilli(dateCommandeFournisseur);
        dto.setDateCommande(dateCmd);
        return commandeFournisseurService.save(dto);
    }

    @Override
    public CommandeFournisseurDto findById(Integer id) {
        return commandeFournisseurService.findById(id);
    }

    @Override
    public CommandeFournisseurDto findByCodeCommande(String code) {
        return commandeFournisseurService.findByCodeCommande(code);
    }

    @Override
    public CommandeFournisseurDto updateEtatCommande(Integer id, EtatCommande etatCommande) {
        return commandeFournisseurService.updateEtatCommande(id, etatCommande);
    }

    @Override
    public CommandeFournisseurDto updateQuantiterCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        return commandeFournisseurService.updateQuantiterCommande(idCommande, idLigneCommande, quantite);
    }

    @Override
    public CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur) {
        return commandeFournisseurService.updateFournisseur(idFournisseur, idFournisseur);
    }

    @Override
    public CommandeFournisseurDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle) {
        return commandeFournisseurService.updateArticle(idCommande, idLigneCommande, newIdArticle);
    }

    @Override
    public CommandeFournisseurDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
        return commandeFournisseurService.deleteArticle(idCommande, idLigneCommande);
    }

    @Override
    public List<LigneCommandeFournisseurDto> findAllByCommandeFournisseur(Integer idCommande) {
        return commandeFournisseurService.findAllByCommandeFournisseur(idCommande);
    }

    @Override
    public List<CommandeFournisseurDto> findByDateCommande(Instant dateCommande) {
        return commandeFournisseurService.findByDateCommande(dateCommande);
    }

    @Override
    public List<CommandeFournisseurDto> findAll() {
        return commandeFournisseurService.findAll();
    }

    @Override
    public void delete(Integer id) {
        commandeFournisseurService.delete(id);
    }
}
