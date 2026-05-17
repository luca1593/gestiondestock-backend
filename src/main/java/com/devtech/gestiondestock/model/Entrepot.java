package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "entrepot")
public class Entrepot extends AbstractEntity {
    @Column(name = "code")
    private String code;
    
    @Column(name = "nom")
    private String nom;
    
    @Column(name = "description")
    private String description;
    
    @Embedded
    private Adresse adresse;
    
    @Column(name = "estPrincipal")
    private Boolean estPrincipal = false;
    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;
}