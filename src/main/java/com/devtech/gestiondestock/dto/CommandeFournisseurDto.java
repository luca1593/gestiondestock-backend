package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.CommandeFournisseur;
import com.devtech.gestiondestock.model.EtatCommande;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CommandeFournisseurDto {
    private Integer id;
    private String code;
    private Instant dateCommande;
    private EtatCommande etatcommande;
    private FournisseurDto fournisseur;
    private List<LigneCommandeFournisseurDto> ligneCommandeFournisseurs;
    private Integer identreprise;

    public static CommandeFournisseurDto fromEntity(CommandeFournisseur commandeFournisseur){
        if (commandeFournisseur == null){
            return null;
        }
        return CommandeFournisseurDto.builder()
                .id(commandeFournisseur.getId())
                .code(commandeFournisseur.getCode())
                .dateCommande(commandeFournisseur.getDateCommande())
                .etatcommande(commandeFournisseur.getEtatcommande())
                .fournisseur(FournisseurDto.fromEntity(commandeFournisseur.getFournisseur()))
                .identreprise(commandeFournisseur.getIdentreprise())
                .build();
    }

    public static CommandeFournisseur toEntity(CommandeFournisseurDto commandeFournisseurDto){
        if (commandeFournisseurDto == null){
            return null;
        }
        CommandeFournisseur commandeFournisseur = new CommandeFournisseur();
        commandeFournisseur.setId(commandeFournisseurDto.getId());
        commandeFournisseur.setCode(commandeFournisseurDto.getCode());
        commandeFournisseur.setDateCommande(commandeFournisseurDto.getDateCommande());
        commandeFournisseur.setEtatcommande(commandeFournisseurDto.getEtatcommande());
        commandeFournisseur.setFournisseur(FournisseurDto.toEntity(commandeFournisseurDto.getFournisseur()));
        commandeFournisseur.setIdentreprise(commandeFournisseurDto.getIdentreprise());
        return commandeFournisseur;
    }

    public boolean isCommandeLivree(){
        return EtatCommande.LIVREE.equals(this.etatcommande);
    }
}
