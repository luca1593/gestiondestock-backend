package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.RegleTarifaire;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class RegleTarifaireDto {
    private Integer id;
    private String code;
    private String nom;
    private String description;
    private RegleTarifaire.TypeRegle typeRegle;
    private BigDecimal valeur;
    private Boolean estActif;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private BigDecimal quantiteMinimale;
    private BigDecimal montantMinimal;
    private Integer entrepriseId;
    private String entrepriseNom;
    private Integer categorieId;
    private String categorieDesignation;
    private Integer clientId;
    private String clientNom;

    public static RegleTarifaireDto fromEntity(RegleTarifaire regle) {
        if (regle == null) return null;
        return RegleTarifaireDto.builder()
                .id(regle.getId())
                .code(regle.getCode())
                .nom(regle.getNom())
                .description(regle.getDescription())
                .typeRegle(regle.getTypeRegle())
                .valeur(regle.getValeur())
                .estActif(regle.getEstActif())
                .dateDebut(regle.getDateDebut())
                .dateFin(regle.getDateFin())
                .quantiteMinimale(regle.getQuantiteMinimale())
                .montantMinimal(regle.getMontantMinimal())
                .entrepriseId(regle.getEntreprise() != null ? regle.getEntreprise().getId() : null)
                .entrepriseNom(regle.getEntreprise() != null ? regle.getEntreprise().getNom() : null)
                .categorieId(regle.getCategorie() != null ? regle.getCategorie().getId() : null)
                .categorieDesignation(regle.getCategorie() != null ? regle.getCategorie().getDesignation() : null)
                .clientId(regle.getClient() != null ? regle.getClient().getId() : null)
                .clientNom(regle.getClient() != null ? regle.getClient().getNom() : null)
                .build();
    }

    public static RegleTarifaire toEntity(RegleTarifaireDto dto) {
        if (dto == null) return null;
        RegleTarifaire regle = new RegleTarifaire();
        regle.setId(dto.getId());
        regle.setCode(dto.getCode());
        regle.setNom(dto.getNom());
        regle.setDescription(dto.getDescription());
        regle.setTypeRegle(dto.getTypeRegle());
        regle.setValeur(dto.getValeur());
        regle.setEstActif(dto.getEstActif());
        regle.setDateDebut(dto.getDateDebut());
        regle.setDateFin(dto.getDateFin());
        regle.setQuantiteMinimale(dto.getQuantiteMinimale());
        regle.setMontantMinimal(dto.getMontantMinimal());
        return regle;
    }
}
