package com.devtech.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertStockDto {
    private Integer id;
    private Integer articleId;
    private String designation;
    private Double stockActuel;
    private Double seuilMinimum;
    private Double seuilCritique;
    private String niveauAlerte;
    private Boolean active;
    private Integer identreprise;

    public static AlertStockDto fromEntity(com.devtech.gestiondestock.model.AlertStock alert) {
        if (alert == null) return null;
        return AlertStockDto.builder()
                .id(alert.getId())
                .articleId(alert.getArticleId())
                .designation(alert.getDesignation())
                .stockActuel(alert.getStockActuel())
                .seuilMinimum(alert.getSeuilMinimum())
                .seuilCritique(alert.getSeuilCritique())
                .niveauAlerte(alert.getNiveauAlerte())
                .active(alert.getActive())
                .identreprise(alert.getIdentreprise())
                .build();
    }
}
