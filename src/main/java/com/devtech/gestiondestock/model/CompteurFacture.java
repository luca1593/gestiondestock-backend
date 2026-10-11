package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Compteur de numerotation des factures, par entreprise et par annee.
 * Permet de generer un numero de facture sequentiel et unique de facon atomique.
 *
 * @author luca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "compteur_facture",
        uniqueConstraints = @UniqueConstraint(name = "UK_compteur_facture", columnNames = {"identreprise", "annee"}))
public class CompteurFacture extends AbstractEntity {
    @Column(name = "identreprise", nullable = false)
    private Integer identreprise;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "valeur", nullable = false)
    private Long valeur = 0L;
}
