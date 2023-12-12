package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "article")
public class Article extends AbstractEntity {
    @Column(name = "codearticle")
    private String codeArticle;
    @Column(name = "designation")
    private String designation;
    @Column(name = "prixunitaireht")
    private BigDecimal prixUnitaireht;
    @Column(name = "tauxtva")
    private BigDecimal tauxTva;
    @Column(name = "prixttc")
    private BigDecimal prixTtc;
    @Column(name = "prixfrs")
    private BigDecimal prixFrs;
    @Column(name = "stock")
    private BigDecimal stock;
    @Column(name = "photo")
    private String photo;
    @ManyToOne
    @JoinColumn(name = "idCategory")
    private Category category;
    @OneToMany(mappedBy = "article")
    private List<LigneVente> ligneVentes;
    @OneToMany(mappedBy = "article")
    private List<LigneCommandeClient> ligneCommandeClients;
    @OneToMany(mappedBy = "article")
    private List<LigneCommandeFournisseur> ligneCommandeFournisseurs;
    @OneToMany(mappedBy = "article")
    private List<MvtStk> mvtStks;
    @ManyToOne
    @JoinColumn(name = "identreprise")
    private Entreprise entreprise;
}
