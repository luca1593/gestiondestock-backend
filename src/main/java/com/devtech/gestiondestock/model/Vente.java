package com.devtech.gestiondestock.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "vente")
public class Vente extends AbstractEntity{
    @Column(name = "code")
    private String code;
    @Column(name = "dateVente")
    private Instant dateVente;
    @Column(name = "commentaire")
    private String commentaire;
    @OneToMany(mappedBy = "vente")
    private List<LigneVente> ligneVentes;
    @Column(name = "identreprise")
    private Integer identreprise;
}
