package com.devtech.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private Long totalArticles;
    private Long totalClients;
    private Long totalFournisseurs;
    private Long totalCommandesClient;
    private Long totalCommandesFournisseur;
    private Long totalVentes;
    private BigDecimal chiffreAffaires;
    private BigDecimal valeurStock;
    private Long articlesStockBas;
    private Long commandesEnAttente;
}
