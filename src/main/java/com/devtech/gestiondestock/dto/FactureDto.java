package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Facture;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class FactureDto {
    private Integer id;
    private String numeroFacture;
    private LocalDateTime dateFacture;
    private LocalDateTime dateEcheance;
    private Facture.StatutFacture statut;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private BigDecimal montantPaye;
    private BigDecimal montantRestant;
    private Integer clientId;
    private String clientNom;
    private Integer entrepriseId;
    private String entrepriseNom;

    public static FactureDto fromEntity(Facture facture) {
        if (facture == null) return null;
        return FactureDto.builder()
                .id(facture.getId())
                .numeroFacture(facture.getNumeroFacture())
                .dateFacture(facture.getDateFacture())
                .dateEcheance(facture.getDateEcheance())
                .statut(facture.getStatut())
                .montantHT(facture.getMontantHT())
                .montantTVA(facture.getMontantTVA())
                .montantTTC(facture.getMontantTTC())
                .montantPaye(facture.getMontantPaye())
                .montantRestant(facture.getMontantRestant())
                .clientId(facture.getClient() != null ? facture.getClient().getId() : null)
                .clientNom(facture.getClient() != null ? facture.getClient().getNom() : null)
                .entrepriseId(facture.getEntreprise() != null ? facture.getEntreprise().getId() : null)
                .entrepriseNom(facture.getEntreprise() != null ? facture.getEntreprise().getNom() : null)
                .build();
    }

    public static Facture toEntity(FactureDto dto) {
        if (dto == null) return null;
        Facture facture = new Facture();
        facture.setId(dto.getId());
        facture.setNumeroFacture(dto.getNumeroFacture());
        facture.setDateFacture(dto.getDateFacture());
        facture.setDateEcheance(dto.getDateEcheance());
        facture.setStatut(dto.getStatut());
        facture.setMontantHT(dto.getMontantHT());
        facture.setMontantTVA(dto.getMontantTVA());
        facture.setMontantTTC(dto.getMontantTTC());
        facture.setMontantPaye(dto.getMontantPaye());
        facture.setMontantRestant(dto.getMontantRestant());
        return facture;
    }
}
