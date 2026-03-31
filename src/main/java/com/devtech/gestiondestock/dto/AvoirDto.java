package com.devtech.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvoirDto {
    private Integer id;
    private String code;
    private Instant dateAvoir;
    private Double montant;
    private String raison;
    private String etat;
    private ClientDto client;
    private VenteDto vente;
    private Integer identreprise;

    public static AvoirDto fromEntity(com.devtech.gestiondestock.model.Avoir avoir) {
        if (avoir == null) return null;
        return AvoirDto.builder()
                .id(avoir.getId())
                .code(avoir.getCode())
                .dateAvoir(avoir.getDateAvoir())
                .montant(avoir.getMontant())
                .raison(avoir.getRaison())
                .etat(avoir.getEtat())
                .client(ClientDto.fromEntity(avoir.getClient()))
                .vente(VenteDto.fromEntity(avoir.getVente()))
                .identreprise(avoir.getIdentreprise())
                .build();
    }

    public static com.devtech.gestiondestock.model.Avoir toEntity(AvoirDto dto) {
        if (dto == null) return null;
        com.devtech.gestiondestock.model.Avoir avoir = new com.devtech.gestiondestock.model.Avoir();
        avoir.setId(dto.getId());
        avoir.setCode(dto.getCode());
        avoir.setDateAvoir(dto.getDateAvoir());
        avoir.setMontant(dto.getMontant());
        avoir.setRaison(dto.getRaison());
        avoir.setEtat(dto.getEtat());
        avoir.setClient(ClientDto.toEntity(dto.getClient()));
        avoir.setVente(VenteDto.toEntity(dto.getVente()));
        avoir.setIdentreprise(dto.getIdentreprise());
        return avoir;
    }
}
