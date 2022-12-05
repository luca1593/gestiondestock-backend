package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Client;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ClientDto {
    private Integer id;
    private String nom;
    private String prenom;
    private AdresseDto adresse;
    private String photo;
    private String email;
    private String numTel;
    private List<CommandeClientDto> commandeClients;
    private Integer identreprise;

    public static ClientDto fromEntity(Client client){
        if (client == null){
            return null;
        }

        return ClientDto.builder()
                .id(client.getId())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .adresse(AdresseDto.fromEntity(client.getAdresse()))
                .photo(client.getPhoto())
                .email(client.getEmail())
                .numTel(client.getNumTel())
                .commandeClients(
                        client.getCommandeclients() != null ?
                                client.getCommandeclients()
                                        .stream()
                                        .map(CommandeClientDto::fromEntity)
                                        .collect(Collectors.toList()) : null
                )
                .identreprise(client.getIdentreprise())
                .build();
    }

    public static Client toEntity(ClientDto clientDto){
        if (clientDto == null){
            return  null;
        }
        Client client = new Client();
        client.setId(clientDto.getId());
        client.setNom(clientDto.getNom());
        client.setPrenom(clientDto.getPrenom());
        client.setAdresse(AdresseDto.toEntity(clientDto.getAdresse()));
        client.setPhoto(clientDto.getPhoto());
        client.setEmail(clientDto.getEmail());
        client.setNumTel(clientDto.getNumTel());
        client.setCommandeclients(
                clientDto.getCommandeClients() != null ?
                        clientDto.getCommandeClients()
                                .stream()
                                .map(CommandeClientDto::toEntity)
                                .collect(Collectors.toList()) : null
        );
        client.setIdentreprise(clientDto.getIdentreprise());
        return client;
    }
}
