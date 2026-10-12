package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "paiement")
public class Paiement extends AbstractEntity {
    @Column(name = "montant")
    private BigDecimal montant;

    /**
     * Somme effectivement remise par le payeur (utile pour le paiement en especes).
     */
    @Column(name = "montantDonne")
    private BigDecimal montantDonne;

    /**
     * Monnaie rendue au payeur. Calculee par le serveur : montantDonne - montant.
     */
    @Column(name = "monnaie")
    private BigDecimal monnaie;

    @Column(name = "datePaiement")
    private LocalDateTime datePaiement;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "modePaiement")
    private ModePaiement modePaiement;
    
    @Column(name = "reference")
    private String reference;
    
    @Column(name = "note")
    private String note;
    
    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;
    @ManyToOne
    @JoinColumn(name = "identreprise")
    private Entreprise entreprise;

    public enum ModePaiement {
        ESPECES, CHEQUE, VIREMENT_BANCAIRE, CARTE_BANCAIRE, MOBILE_MONEY, AUTRE
    }
}