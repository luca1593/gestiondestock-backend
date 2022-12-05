package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Vente;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class VenteDto {
    private Integer id;
    private String code;
    private Instant dateVente;
    private String commentaire;
    private List<LigneVenteDto> ligneVentes;
    private Integer identreprise;

    public static VenteDto fromEntity(Vente vente){
        if (vente == null){
            return null;
        }
        return VenteDto.builder()
                .id(vente.getId())
                .code(vente.getCode())
                .dateVente(vente.getDateVente())
                .commentaire(vente.getCommentaire())
                .ligneVentes(vente.getLigneVentes() != null ?
                        vente.getLigneVentes().stream()
                                .map(LigneVenteDto::fromEntity)
                                .collect(Collectors.toList()) : null
                )
                .identreprise(vente.getIdentreprise())
                .build();
    }

    public static Vente toEntity(VenteDto venteDto){
        if(venteDto == null){
            return null;
        }

        Vente vente = new Vente();
        vente.setId(venteDto.getId());
        vente.setCode(venteDto.getCode());
        vente.setDateVente(venteDto.getDateVente());
        vente.setCommentaire(venteDto.getCommentaire());
        vente.setLigneVentes(venteDto.ligneVentes != null ?
                venteDto.ligneVentes
                        .stream()
                        .map(LigneVenteDto::toEntity)
                        .collect(Collectors.toList()) : null
        );
        vente.setIdentreprise(venteDto.getIdentreprise());

        return vente;
    }
}
