package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Inventaire;
import com.devtech.gestiondestock.model.InventaireLigne;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InventaireLigneDto {
    private Integer id;
    private Integer inventaireId;
    private Integer articleId;
    private String articleDesignation;
    private BigDecimal quantiteTheorique;
    private BigDecimal quantiteReelle;

    public static InventaireLigneDto fromEntity(InventaireLigne ligne) {
        if (ligne == null) return null;
        return InventaireLigneDto.builder()
                .id(ligne.getId())
                .inventaireId(ligne.getInventaire() != null ? ligne.getInventaire().getId() : null)
                .articleId(ligne.getArticle() != null ? ligne.getArticle().getId() : null)
                .articleDesignation(ligne.getArticle() != null ? ligne.getArticle().getDesignation() : null)
                .quantiteTheorique(ligne.getQuantiteTheorique())
                .quantiteReelle(ligne.getQuantiteReelle())
                .build();
    }

    public static InventaireLigne toEntity(InventaireLigneDto dto) {
        if (dto == null) return null;
        InventaireLigne ligne = new InventaireLigne();
        ligne.setId(dto.getId());
        ligne.setQuantiteTheorique(dto.getQuantiteTheorique());
        ligne.setQuantiteReelle(dto.getQuantiteReelle());
        return ligne;
    }
}
