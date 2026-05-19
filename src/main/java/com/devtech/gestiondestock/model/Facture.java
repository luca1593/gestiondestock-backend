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
    
    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "identreprise", insertable = false, updatable = false)
    private Entreprise entreprise;

    public enum StatutFacture {
        EN_ATTENTE, PARTIELLEMENT_PAYEE, PAYEE, EN_RETARD, ANNULEE
    }
}