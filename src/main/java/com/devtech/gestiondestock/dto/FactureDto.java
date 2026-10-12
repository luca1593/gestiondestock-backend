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
    private Facture.TypeFacture typeFacture;
    private Facture.SourceDocument sourceType;
    private Integer sourceId;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private BigDecimal montantPaye;
    private BigDecimal montantRestant;
    private Integer clientId;
    private String clientNom;
    private Integer fournisseurId;
    private String fournisseurNom;
    private Integer entrepriseId;
    private String entrepriseNom;

    /**
     * Informations de paiement, utilisees uniquement en entree de
     * POST /v1/factures/save-avec-paiement. Non renseigne en lecture
     * (utiliser GET /v1/paiements/facture/{id}).
     */
    private PaiementDto paiement;

    public static FactureDto fromEntity(Facture facture) {
        if (facture == null) return null;
        return FactureDto.builder()
                .id(facture.getId())
                .numeroFacture(facture.getNumeroFacture())
                .dateFacture(facture.getDateFacture())
                .dateEcheance(facture.getDateEcheance())
                .statut(facture.getStatut())
                .typeFacture(facture.getTypeFacture())
                .sourceType(facture.getSourceType())
                .sourceId(facture.getSourceId())
                .montantHT(facture.getMontantHT())
                .montantTVA(facture.getMontantTVA())
                .montantTTC(facture.getMontantTTC())
                .montantPaye(facture.getMontantPaye())
                .montantRestant(facture.getMontantRestant())
                .clientId(facture.getClient() != null ? facture.getClient().getId() : null)
                .clientNom(facture.getClient() != null ? facture.getClient().getNom() : null)
                .fournisseurId(facture.getFournisseur() != null ? facture.getFournisseur().getId() : null)
                .fournisseurNom(facture.getFournisseur() != null ? facture.getFournisseur().getNom() : null)
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
        facture.setTypeFacture(dto.getTypeFacture());
        facture.setSourceType(dto.getSourceType());
        facture.setSourceId(dto.getSourceId());
        facture.setMontantHT(dto.getMontantHT());
        facture.setMontantTVA(dto.getMontantTVA());
        facture.setMontantTTC(dto.getMontantTTC());
        facture.setMontantPaye(dto.getMontantPaye());
        facture.setMontantRestant(dto.getMontantRestant());
        return facture;
    }
}
