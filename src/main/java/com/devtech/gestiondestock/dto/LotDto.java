package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Lot;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class LotDto {
    private Integer id;
    private String numeroLot;
    private BigDecimal quantite;
    private BigDecimal quantiteRestante;
    private LocalDateTime dateFabrication;
    private LocalDateTime dateExpiration;
    private BigDecimal prixAchat;
    private Integer articleId;
    private String articleDesignation;
    private Integer entrepotId;
    private String entrepotNom;
    private Integer entrepriseId;
    private String entrepriseNom;
    private Boolean estCompletementUtilise;

    public static LotDto fromEntity(Lot lot) {
        if (lot == null) return null;
        return LotDto.builder()
                .id(lot.getId())
                .numeroLot(lot.getNumeroLot())
                .quantite(lot.getQuantite())
                .quantiteRestante(lot.getQuantiteRestante())
                .dateFabrication(lot.getDateFabrication())
                .dateExpiration(lot.getDateExpiration())
                .prixAchat(lot.getPrixAchat())
                .articleId(lot.getArticle() != null ? lot.getArticle().getId() : null)
                .articleDesignation(lot.getArticle() != null ? lot.getArticle().getDesignation() : null)
                .entrepotId(lot.getEntrepot() != null ? lot.getEntrepot().getId() : null)
                .entrepotNom(lot.getEntrepot() != null ? lot.getEntrepot().getNom() : null)
                .entrepriseId(lot.getEntreprise() != null ? lot.getEntreprise().getId() : null)
                .entrepriseNom(lot.getEntreprise() != null ? lot.getEntreprise().getNom() : null)
                .estCompletementUtilise(lot.getEstCompletementUtilise())
                .build();
    }

    public static Lot toEntity(LotDto dto) {
        if (dto == null) return null;
        Lot lot = new Lot();
        lot.setId(dto.getId());
        lot.setNumeroLot(dto.getNumeroLot());
        lot.setQuantite(dto.getQuantite());
        lot.setQuantiteRestante(dto.getQuantiteRestante());
        lot.setDateFabrication(dto.getDateFabrication());
        lot.setDateExpiration(dto.getDateExpiration());
        lot.setPrixAchat(dto.getPrixAchat());
        lot.setEstCompletementUtilise(dto.getEstCompletementUtilise());
        return lot;
    }
}
