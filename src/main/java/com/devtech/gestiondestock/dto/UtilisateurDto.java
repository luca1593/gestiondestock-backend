package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Utilisateur;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UtilisateurDto {
    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private Instant dateDeNaissance;
    private String motDePasse;
    private AdresseDto adresse;
    private String photo;
    private EntrepriseDto entreprise;
    private List<RoleDto> roles;

    public static UtilisateurDto fromEntity(Utilisateur utilisateur){
        if (utilisateur == null){
            return null;
        }

        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .dateDeNaissance(utilisateur.getDateDeNaissance())
                .motDePasse(utilisateur.getMotDePasse())
                .adresse(AdresseDto.fromEntity(utilisateur.getAdresse()))
                .photo(utilisateur.getPhoto())
                .roles(
                        utilisateur.getRoles() != null ?
                                utilisateur.getRoles()
                                        .stream()
                                        .map(RoleDto::fromEntity)
                                        .collect(Collectors.toList()) : null
                )
                .entreprise(EntrepriseDto.fromEntity(utilisateur.getEntreprise()))
                .build();
    }

    public static Utilisateur toEntity(UtilisateurDto utilisateurDto){
        if (utilisateurDto == null){
            return null;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurDto.getId());
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setDateDeNaissance(utilisateurDto.getDateDeNaissance());
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setAdresse(AdresseDto.toEntity(utilisateurDto.getAdresse()));
        utilisateur.setPhoto(utilisateurDto.getPhoto());
        utilisateur.setRoles(
                utilisateurDto.getRoles() != null ?
                        utilisateurDto.getRoles()
                                .stream()
                                .map(RoleDto::toEntity)
                                .collect(Collectors.toList()) : null
        );
        utilisateur.setEntreprise(EntrepriseDto.toEntity(utilisateurDto.getEntreprise()));

        return utilisateur;
    }
}
