package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.TransfertStock;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
public class TransfertStockDto {
    private Integer id;
    private String code;
    private LocalDateTime dateTransfert;
    private TransfertStock.StatutTransfert statut;
    private Integer entrepriseId;
    private String entrepriseNom;
    private Integer entrepotSourceId;
    private String entrepotSourceNom;
    private Integer entrepotDestinationId;
    private String entrepotDestinationNom;

    public static TransfertStockDto fromEntity(TransfertStock transfert) {
        if (transfert == null) return null;
        return TransfertStockDto.builder()
                .id(transfert.getId())
                .code(transfert.getCode())
                .dateTransfert(transfert.getDateTransfert())
                .statut(transfert.getStatut())
                .entrepriseId(transfert.getEntreprise() != null ? transfert.getEntreprise().getId() : null)
                .entrepriseNom(transfert.getEntreprise() != null ? transfert.getEntreprise().getNom() : null)
                .entrepotSourceId(transfert.getEntrepotSource() != null ? transfert.getEntrepotSource().getId() : null)
                .entrepotSourceNom(transfert.getEntrepotSource() != null ? transfert.getEntrepotSource().getNom() : null)
                .entrepotDestinationId(transfert.getEntrepotDestination() != null ? transfert.getEntrepotDestination().getId() : null)
                .entrepotDestinationNom(transfert.getEntrepotDestination() != null ? transfert.getEntrepotDestination().getNom() : null)
                .build();
    }

    public static TransfertStock toEntity(TransfertStockDto dto) {
        if (dto == null) return null;
        TransfertStock transfert = new TransfertStock();
        transfert.setId(dto.getId());
        transfert.setCode(dto.getCode());
        transfert.setDateTransfert(dto.getDateTransfert());
        transfert.setStatut(dto.getStatut());
        return transfert;
    }
}
