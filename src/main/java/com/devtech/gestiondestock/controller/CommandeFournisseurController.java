package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.CommandeFournisseurApi;
import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.services.CommandeFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * @author luca
 */
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
        return this.commandeFournisseurService.save(dto);
    }

    @Override
    public CommandeFournisseurDto findById(Integer id) {
        return this.commandeFournisseurService.findById(id);
    }

    @Override
    public CommandeFournisseurDto findByCodeCommande(String code) {
        return this.commandeFournisseurService.findByCodeCommande(code);
    }

    @Override
    public CommandeFournisseurDto updateEtatCommande(Integer id, EtatCommande etatCommande) {
        return this.commandeFournisseurService.updateEtatCommande(id, etatCommande);
    }

    @Override
    public CommandeFournisseurDto updateQuantiterCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        return this.commandeFournisseurService.updateQuantiterCommande(idCommande, idLigneCommande, quantite);
    }

    @Override
    public CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur) {
        return this.commandeFournisseurService.updateFournisseur(idFournisseur, idFournisseur);
    }

    @Override
    public CommandeFournisseurDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle) {
        return this.commandeFournisseurService.updateArticle(idCommande, idLigneCommande, newIdArticle);
    }

    @Override
    public CommandeFournisseurDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
        return this.commandeFournisseurService.deleteArticle(idCommande, idLigneCommande);
    }

    @Override
    public List<LigneCommandeFournisseurDto> findAllByCommandeFournisseur(Integer idCommande) {
        return this.commandeFournisseurService.findAllByCommandeFournisseur(idCommande);
    }

    @Override
    public List<CommandeFournisseurDto> findByDateCommande(Instant dateCommande) {
        return this.commandeFournisseurService.findByDateCommande(dateCommande);
    }

    @Override
    public List<CommandeFournisseurDto> findAll() {
        return this.commandeFournisseurService.findAll();
    }

    @Override
    public List<CommandeFournisseurDto> findAllByFournisseur(FournisseurDto dto) {
        return this.commandeFournisseurService.findAllByFournisseurDto(dto);
    }

    @Override
    public void delete(Integer id) {
        this.commandeFournisseurService.delete(id);
    }
}
