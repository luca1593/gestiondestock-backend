package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.ArticleApi;
import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luca
 */
@RestController
public class ArticleController implements ArticleApi {
    private ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    @Override
    public ArticleDto save(ArticleDto dto) {
        return this.articleService.save(dto);
    }

    @Override
    public ArticleDto findById(Integer id) {
        return this.articleService.findById(id);
    }

    @Override
    public ArticleDto findByCodeArticle(String code) {
        return this.articleService.findByCodeArticle(code);
    }

    @Override
    public List<ArticleDto> findAll() {
        return this.articleService.findAll();
    }

    @Override
    public List<LigneVenteDto> findHistoriqueVente(Integer idArticle) {
        return this.articleService.findHistoriqueVente(idArticle);
    }

    @Override
    public List<LigneCommandeClientDto> findHistoriqueCommandeClient(Integer idArticle) {
        return this.articleService.findHistoriqueCommandeClient(idArticle);
    }

    @Override
    public List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(Integer idArticle) {
        return this.articleService.findHistoriqueCommandeFournisseur(idArticle);
    }

    @Override
    public List<ArticleDto> findAllByCategorie(Integer idCategorie) {
        return this.articleService.findAllByCategorie(idCategorie);
    }

    @Override
    public void delete(Integer id) {
        this.articleService.delete(id);
    }
}
