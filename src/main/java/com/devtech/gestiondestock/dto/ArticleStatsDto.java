package com.devtech.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleStatsDto {
    private Integer articleId;
    private String codeArticle;
    private String designation;
    private BigDecimal stock;
    private BigDecimal prixUnitaire;
    private String category;
    private Long nbVentes;
    private Long nbCommandesClient;
    private Long nbCommandesFournisseur;
}
