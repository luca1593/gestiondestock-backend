package com.devtech.gestiondestock.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ligneVente")
public class LigneVente extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "idvente")
    private Vente vente;
    @ManyToOne
    @JoinColumn(name = "idarticle")
    private Article article;
    @Column(name = "quantite")
    private BigDecimal quantite;
    @Column(name = "prixUnitaire")
    private BigDecimal prixUnitaire;
    @Column(name = "identreprise")
    private Integer identreprise;
}
