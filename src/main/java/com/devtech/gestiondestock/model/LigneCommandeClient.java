package com.devtech.gestiondestock.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ligneCommandeClient")
public class LigneCommandeClient extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "idarticle")
    private Article article;
    @ManyToOne
    @JoinColumn(name = "idcommandeclient")
    private CommandeClient commandeClient;
    @Column(name = "quantite")
    private BigDecimal quantite;
    @Column(name = "prixunitaire")
    private BigDecimal prixUnitaire;
    @Column(name = "identreprise")
    private Integer identreprise;
}
