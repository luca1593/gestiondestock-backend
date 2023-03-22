package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Fournisseur;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class FournisseurDto {
    private Integer id;
    private String nom;
    private String prenom;
    private AdresseDto adresse;
    private String photo;
    private String email;
    private String numTel;
    @JsonIgnore
    private List<CommandeFournisseurDto> commandeFournisseurs;
    private Integer identreprise;

    public static FournisseurDto fromEntity(Fournisseur fournisseur){
        if (fournisseur == null){
            return null;
        }

        return FournisseurDto.builder()
                .id(fournisseur.getId())
                .nom(fournisseur.getNom())
                .prenom(fournisseur.getPrenom())
                .adresse(AdresseDto.fromEntity(fournisseur.getAdresse()))
                .photo(fournisseur.getPhoto())
                .email(fournisseur.getEmail())
                .numTel(fournisseur.getNumTel())
                .identreprise(fournisseur.getIdentreprise())
                .build();
    }

    public static Fournisseur toEntity(FournisseurDto fournisseurDto){
        if (fournisseurDto == null){
            return null;
        }
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(fournisseurDto.getId());
        fournisseur.setNom(fournisseurDto.getNom());
        fournisseur.setPrenom(fournisseurDto.getPrenom());
        fournisseur.setAdresse(AdresseDto.toEntity(fournisseurDto.getAdresse()));
        fournisseur.setPhoto(fournisseurDto.getPhoto());
        fournisseur.setEmail(fournisseurDto.getEmail());
        fournisseur.setNumTel(fournisseurDto.getNumTel());
        fournisseur.setIdentreprise(fournisseurDto.getIdentreprise());
        return fournisseur;
    }


}
