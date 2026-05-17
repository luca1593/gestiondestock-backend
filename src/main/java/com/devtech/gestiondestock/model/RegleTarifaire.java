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
@AttributeOverride(name = "identreprise", column = @Column(name = "identreprise"))
@Entity
@Table(name = "regle_tarifaire")
public class RegleTarifaire extends AbstractEntity {
    @Column(name = "code")
    private String code;
    
    @Column(name = "nom")
    private String nom;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "typeRegle")
    private TypeRegle typeRegle;
    
    @Column(name = "valeur")
    private BigDecimal valeur;
    
    @Column(name = "estActif")
    private Boolean estActif = true;
    
    @Column(name = "dateDebut")
    private LocalDateTime dateDebut;
    
    @Column(name = "dateFin")
    private LocalDateTime dateFin;
    
    @Column(name = "quantiteMinimale")
    private BigDecimal quantiteMinimale;
    
    @Column(name = "montantMinimal")
    private BigDecimal montantMinimal;
    
    @ManyToOne
    @JoinColumn(name = "identreprise", insertable = false, updatable = false)
    private Entreprise entreprise;
    
    @ManyToOne
    @JoinColumn(name = "idCategory")
    private Category categorie;
    
    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client client;
    
    public enum TypeRegle {
        REMISE_POURCENTAGE, REMISE_MONTANT_FIXE, PRIX_SPECIFIQUE, PROMOTION
    }
}