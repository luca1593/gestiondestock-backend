package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventaire")
public class Inventaire extends AbstractEntity {
    @Column(name = "code")
    private String code;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "dateDebut")
    private LocalDateTime dateDebut;
    
    @Column(name = "dateFin")
    private LocalDateTime dateFin;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private Statut statut;
    
    @ManyToOne
    @JoinColumn(name = "identreprise")
    private Entreprise entreprise;
    
    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private Entrepot entrepot;
    
    public enum Statut {
        EN_COURS, TERMINE, ANNULE
    }
}