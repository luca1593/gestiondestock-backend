package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.CommandeFournisseurApi;
import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.services.CommandeFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
public class CommandeFournisseurController implements CommandeFournisseurApi {

    private final CommandeFournisseurService commandeFournisseurService;

    @Autowired
    public CommandeFournisseurController(CommandeFournisseurService commandeFournisseurService) {
        this.commandeFournisseurService = commandeFournisseurService;
    }

    @Override
    public CommandeFournisseurDto save(@RequestBody CommandeFournisseurDto dto, Long dateCommandeFournisseur) {
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
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public CommandeFournisseurDto updateEtatCommande(Integer id, EtatCommande etatCommande) {
        return this.commandeFournisseurService.updateEtatCommande(id, etatCommande);
    }

    @Override
    public CommandeFournisseurDto updateQuantiterCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        return this.commandeFournisseurService.updateQuantiterCommande(idCommande, idLigneCommande, quantite);
    }

    @Override
    public CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur) {
        return this.commandeFournisseurService.updateFournisseur(idCommande, idFournisseur);
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
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public void delete(Integer id) {
        this.commandeFournisseurService.delete(id);
    }
}
