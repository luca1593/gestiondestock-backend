package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "entrepot_id")
    private Entrepot entrepot;

    @OneToMany(mappedBy = "inventaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventaireLigne> lignes;
    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;

    public enum Statut {
        EN_COURS, TERMINE, ANNULE
    }
}