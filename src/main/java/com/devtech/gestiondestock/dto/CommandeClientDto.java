package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import com.devtech.gestiondestock.model.EtatCommande;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CommandeClientDto {
    private Integer id;
    private String code;
    private Instant dateCommande;
    private EtatCommande etatcommande;
    private ClientDto client;
    @JsonIgnore
    private List<LigneCommandeClientDto> ligneCommandeClients;
    private Integer identreprise;

    public static CommandeClientDto fromEntity(CommandeClient commandeClient){
        if (commandeClient == null){
            return null;
        }
        return CommandeClientDto.builder()
                .id(commandeClient.getId())
                .code(commandeClient.getCode())
                .dateCommande(commandeClient.getDateCommande())
                .etatcommande(commandeClient.getEtatcommande())
                .etatcommande(commandeClient.getEtatcommande())
                .identreprise(commandeClient.getIdentreprise())
                .build();
    }

    public static CommandeClient toEntity(CommandeClientDto commandeClientDto){
        if (commandeClientDto == null){
            return null;
        }
        CommandeClient commandeClient = new CommandeClient();
        commandeClient.setId(commandeClientDto.getId());
        commandeClient.setCode(commandeClientDto.getCode());
        commandeClient.setDateCommande(commandeClientDto.getDateCommande());
        commandeClient.setEtatcommande(commandeClientDto.getEtatcommande());
        commandeClient.setEtatcommande(commandeClientDto.getEtatcommande());
        commandeClient.setIdentreprise(commandeClientDto.getIdentreprise());
        return commandeClient;
    }

    public boolean isCommandeLivree(){
        return EtatCommande.LIVREE.equals(this.etatcommande);
    }
}
