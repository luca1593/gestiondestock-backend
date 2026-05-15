package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Inventaire;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
public class InventaireDto {
    private Integer id;
    private String code;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Inventaire.Statut statut;
    private Integer entrepriseId;
    private String entrepriseNom;
    private Integer entrepotId;
    private String entrepotNom;

    public static InventaireDto fromEntity(Inventaire inventaire) {
        if (inventaire == null) return null;
        return InventaireDto.builder()
                .id(inventaire.getId())
                .code(inventaire.getCode())
                .description(inventaire.getDescription())
                .dateDebut(inventaire.getDateDebut())
                .dateFin(inventaire.getDateFin())
                .statut(inventaire.getStatut())
                .entrepriseId(inventaire.getEntreprise() != null ? inventaire.getEntreprise().getId() : null)
                .entrepriseNom(inventaire.getEntreprise() != null ? inventaire.getEntreprise().getNom() : null)
                .entrepotId(inventaire.getEntrepot() != null ? inventaire.getEntrepot().getId() : null)
                .entrepotNom(inventaire.getEntrepot() != null ? inventaire.getEntrepot().getNom() : null)
                .build();
    }

    public static Inventaire toEntity(InventaireDto dto) {
        if (dto == null) return null;
        Inventaire inventaire = new Inventaire();
        inventaire.setId(dto.getId());
        inventaire.setCode(dto.getCode());
        inventaire.setDescription(dto.getDescription());
        inventaire.setDateDebut(dto.getDateDebut());
        inventaire.setDateFin(dto.getDateFin());
        inventaire.setStatut(dto.getStatut());
        return inventaire;
    }
}
