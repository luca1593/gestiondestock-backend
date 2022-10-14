package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.model.EtatCommande;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface CommandeFournisseurService {
    CommandeFournisseurDto save(CommandeFournisseurDto dto);
    CommandeFournisseurDto findById(Integer id);
    CommandeFournisseurDto findByCodeCommande(String code);
    CommandeFournisseurDto updateEtatCommande(Integer id, EtatCommande etatCommande);
    CommandeFournisseurDto updateQuantiterCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite);
    CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur);
    CommandeFournisseurDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle);
    CommandeFournisseurDto deleteArticle(Integer idCommande, Integer idLigneCommande);
    List<LigneCommandeFournisseurDto> findAllByCommandeFournisseur(Integer idCommande);
    List<CommandeFournisseurDto> findByDateCommande(Instant dateCommande);
    List<CommandeFournisseurDto> findAll();
    void delete(Integer id);
}
