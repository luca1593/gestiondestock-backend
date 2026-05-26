package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventaireLigne")
public class InventaireLigne extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "idinventaire")
    private Inventaire inventaire;

    @ManyToOne
    @JoinColumn(name = "idarticle")
    private Article article;

    @Column(name = "quantite_theorique")
    private BigDecimal quantiteTheorique;

    @Column(name = "quantite_reelle")
    private BigDecimal quantiteReelle;

    @Column(name = "identreprise")
    private Integer identreprise;
}
