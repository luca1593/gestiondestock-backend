package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class ArticleDto {
    private Integer id;
    private String codeArticle;
    private String designation;
    private BigDecimal prixUnitaireht;
    private BigDecimal tauxTva;
    private BigDecimal prixTtc;
    private String photo;
    private CategoryDto category;
    @JsonIgnore
    private List<LigneVenteDto> ligneVentes;
    @JsonIgnore
    private List<LigneCommandeClientDto> ligneCommandeClients;
    @JsonIgnore
    private List<LigneCommandeFournisseurDto> ligneCommandeFournisseurs;
    @JsonIgnore
    private List<MvtStkDto> mvtStks;


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
                .photo(article.getPhoto())
                .category(CategoryDto.fromEntity(article.getCategory()))
                .ligneVentes(
                        article.getLigneVentes() != null ?
                                article.getLigneVentes()
                                        .stream()
                                        .map(LigneVenteDto::fromEntity)
                                        .collect(Collectors.toList()) : null
                )
                .ligneCommandeClients(
                        article.getLigneCommandeClients() != null ?
                                article.getLigneCommandeClients()
                                        .stream()
                                        .map(LigneCommandeClientDto::fromEntity)
                                        .collect(Collectors.toList()) : null
                )
                .ligneCommandeFournisseurs(
                        article.getLigneCommandeFournisseurs() != null ?
                                article.getLigneCommandeFournisseurs()
                                        .stream()
                                        .map(LigneCommandeFournisseurDto::fromEntity)
                                        .collect(Collectors.toList()) : null
                )
                .mvtStks(
                        article.getMvtStks() != null ?
                                article.getMvtStks()
                                        .stream()
                                        .map(MvtStkDto::fromEntity)
                                        .collect(Collectors.toList()) : null
                )
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
        article.setPhoto(articleDto.getPhoto());
        article.setCategory(CategoryDto.toEntity(articleDto.getCategory()));

        article.setLigneVentes(
                articleDto.getLigneVentes() != null ?
                        articleDto.getLigneVentes()
                                .stream()
                                .map(LigneVenteDto::toEntity)
                                .collect(Collectors.toList()) : null
        );
        article.setLigneCommandeClients(
                articleDto.getLigneCommandeClients() != null ?
                        articleDto.getLigneCommandeClients()
                                .stream()
                                .map(LigneCommandeClientDto::toEntity)
                                .collect(Collectors.toList()) : null
        );

        article.setMvtStks(
                articleDto.getMvtStks() != null ?
                        articleDto.getMvtStks()
                                .stream()
                                .map(MvtStkDto::toEntity)
                                .collect(Collectors.toList()) : null
        );

        return article;
    }
}
