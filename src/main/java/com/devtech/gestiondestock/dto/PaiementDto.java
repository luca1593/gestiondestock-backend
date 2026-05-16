package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Paiement;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class PaiementDto {
    private Integer id;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private Paiement.ModePaiement modePaiement;
    private String reference;
    private String note;
    private Integer factureId;
    private String factureNumero;
    private Integer entrepriseId;
    private String entrepriseNom;

    public static PaiementDto fromEntity(Paiement paiement) {
        if (paiement == null) return null;
        return PaiementDto.builder()
                .id(paiement.getId())
                .montant(paiement.getMontant())
                .datePaiement(paiement.getDatePaiement())
                .modePaiement(paiement.getModePaiement())
                .reference(paiement.getReference())
                .note(paiement.getNote())
                .factureId(paiement.getFacture() != null ? paiement.getFacture().getId() : null)
                .factureNumero(paiement.getFacture() != null ? paiement.getFacture().getNumeroFacture() : null)
                .entrepriseId(paiement.getEntreprise() != null ? paiement.getEntreprise().getId() : null)
                .entrepriseNom(paiement.getEntreprise() != null ? paiement.getEntreprise().getNom() : null)
                .build();
    }

    public static Paiement toEntity(PaiementDto dto) {
        if (dto == null) return null;
        Paiement paiement = new Paiement();
        paiement.setId(dto.getId());
        paiement.setMontant(dto.getMontant());
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setModePaiement(dto.getModePaiement());
        paiement.setReference(dto.getReference());
        paiement.setNote(dto.getNote());
        return paiement;
    }
}
