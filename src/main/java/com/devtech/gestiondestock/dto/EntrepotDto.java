package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Adresse;
import lombok.Data;

@Data
public class EntrepotDto {
    private Integer id;
    private String code;
    private String nom;
    private String description;
    private Adresse adresse;
    private Boolean estPrincipal;
    private Integer entrepriseId;
    private String entrepriseNom;
}