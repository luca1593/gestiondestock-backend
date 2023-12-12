package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Data
public class ArticleDto {
    private Integer id;
    private String codeArticle;
    private String designation;
    private BigDecimal prixUnitaireht;
    private BigDecimal tauxTva;
    private BigDecimal prixTtc;
    private BigDecimal prixFrs;
    private BigDecimal stock;
    private String photo;
    private CategoryDto category;
    private Instant creationDate;
    private Instant lastModifiedDate;
    @JsonIgnore
    private List<LigneVenteDto> ligneVentes;
    @JsonIgnore
    private List<LigneCommandeClientDto> ligneCommandeClients;
    @JsonIgnore
    private List<LigneCommandeFournisseurDto> ligneCommandeFournisseurs;
    @JsonIgnore
    private List<MvtStkDto> mvtStks;
    private EntrepriseDto entreprise;


    public static ArticleDto fromEntity(Article article){
        if (article == null){
            return null;
        }
        return ArticleDto.builder()
                .id(article.getId())
                .codeArticle(article.getCodeArticle())
                .designation(article.getDesignation())
                .prixUnitaireht(article.getPrixUnitaireht())
                .tauxTva(article.getTauxTva())
                .prixTtc(article.getPrixTtc())
                .prixFrs(article.getPrixFrs())
                .stock(article.getStock())
                .photo(article.getPhoto())
                .creationDate(article.getCreationDate())
                .lastModifiedDate(article.getLastModifiedDate())
                .category(CategoryDto.fromEntity(article.getCategory()))
                .entreprise(EntrepriseDto.fromEntity(article.getEntreprise()))
                .build();
    }

    public static Article toEntity(ArticleDto articleDto){
        if (articleDto == null){
            return null;
        }
        Article article = new Article();
        article.setId(articleDto.getId());
        article.setCodeArticle(articleDto.getCodeArticle());
        article.setDesignation(articleDto.getDesignation());
        article.setPrixUnitaireht(articleDto.getPrixUnitaireht());
        article.setTauxTva(articleDto.getTauxTva());
        article.setPrixTtc(articleDto.getPrixTtc());
        article.setPrixFrs(articleDto.getPrixFrs());
        article.setStock(articleDto.getStock());
        article.setPhoto(articleDto.getPhoto());
        article.setCategory(CategoryDto.toEntity(articleDto.getCategory()));
        article.setEntreprise(EntrepriseDto.toEntity(articleDto.getEntreprise()));

        return article;
    }
}
