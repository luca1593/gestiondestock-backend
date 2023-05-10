package com.devtech.gestiondestock.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "role")
public class Role extends AbstractEntity{
    @Column(name = "roleNom")
    private String roleNom;
    @ManyToOne
    @JoinColumn(name = "idutilisateur")
    private Utilisateur utilisateur;

}
