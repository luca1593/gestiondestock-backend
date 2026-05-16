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
@Table(name = "lot")
public class Lot extends AbstractEntity {
    @Column(name = "numeroLot", unique = true, nullable = false)
    private String numeroLot;
    
    @Column(name = "quantite")
    private BigDecimal quantite;
    
    @Column(name = "quantiteRestante")
    private BigDecimal quantiteRestante;
    
    @Column(name = "dateFabrication")
    private LocalDateTime dateFabrication;
    
    @Column(name = "dateExpiration")
    private LocalDateTime dateExpiration;
    
    @Column(name = "prixAchat")
    private BigDecimal prixAchat;
    
    @ManyToOne
    @JoinColumn(name = "idArticle")
    private Article article;
    
    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private Entrepot entrepot;
    
    @ManyToOne
    @JoinColumn(name = "identreprise")
    private Entreprise entreprise;
    
    @Column(name = "estCompletementUtilise")
    private Boolean estCompletementUtilise = false;
}