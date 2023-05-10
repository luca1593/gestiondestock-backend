package com.devtech.gestiondestock.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "fournisseur")
public class Fournisseur extends AbstractEntity{
    @Column(name = "nom")
    private String nom;
    @Column(name = "prenom")
    private String prenom;
    @Embedded
    private Adresse adresse;
    @Column(name = "photo")
    private String photo;
    @Column(name = "email")
    private String email;
    @Column(name = "numTel")
    private String numTel;
    @OneToMany(mappedBy = "fournisseur")
    private List<CommandeFournisseur> commandeFournisseurs;

    @Column(name = "identreprise")
    private Integer identreprise;
}
