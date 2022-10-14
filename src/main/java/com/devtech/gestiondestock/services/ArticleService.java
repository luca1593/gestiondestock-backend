package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.model.LigneCommandeClient;
import com.devtech.gestiondestock.model.LigneCommandeFournisseur;

import java.util.List;

public interface ArticleService {
    ArticleDto save(ArticleDto dto);
    ArticleDto findById(Integer id);
    ArticleDto findByCodeArticle(String code);
    List<ArticleDto> findAll();
    List<LigneVenteDto> findHistoriqueVente(Integer idArticle);
    List<LigneCommandeClientDto> findHistoriqueCommandeClient(Integer idArticle);
    List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(Integer idArticle);
    List<ArticleDto> findAllByCategorie(Integer idCategorie);
    void delete(Integer id);
}
