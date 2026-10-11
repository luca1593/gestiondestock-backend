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
@Table(name = "facture")
public class Facture extends AbstractEntity {
    @Column(name = "numeroFacture", unique = true, nullable = false)
    private String numeroFacture;
    
    @Column(name = "dateFacture")
    private LocalDateTime dateFacture;
    
    @Column(name = "dateEcheance")
    private LocalDateTime dateEcheance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutFacture statut;
    
    @Column(name = "montantHT")
    private BigDecimal montantHT;
    
    @Column(name = "montantTVA")
    private BigDecimal montantTVA;
    
    @Column(name = "montantTTC")
    private BigDecimal montantTTC;
    
    @Column(name = "montantPaye")
    private BigDecimal montantPaye;
    
    @Column(name = "montantRestant")
    private BigDecimal montantRestant;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "typeFacture", nullable = false)
    private TypeFacture typeFacture = TypeFacture.CLIENT;

    /**
     * Document source de la facture (lien polymorphe : type + identifiant).
     * L'integrite est verifiee cote service (pas de contrainte FK).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sourceType")
    private SourceDocument sourceType;

    @Column(name = "sourceId")
    private Integer sourceId;

    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "idfournisseur")
    private Fournisseur fournisseur;

    @ManyToOne
    @JoinColumn(name = "identreprise")
    private Entreprise entreprise;

    public enum StatutFacture {
        EN_ATTENTE, PARTIELLEMENT_PAYEE, PAYEE, EN_RETARD, ANNULEE
    }

    public enum TypeFacture {
        CLIENT, FOURNISSEUR
    }

    public enum SourceDocument {
        VENTE, COMMANDE_CLIENT, COMMANDE_FOURNISSEUR
    }
}