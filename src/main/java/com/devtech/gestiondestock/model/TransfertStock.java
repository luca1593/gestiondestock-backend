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
@Table(name = "transfert_stock")
public class TransfertStock extends AbstractEntity {
    @Column(name = "code")
    private String code;
    
    @Column(name = "dateTransfert")
    private LocalDateTime dateTransfert;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutTransfert statut;
    
    @ManyToOne
    @JoinColumn(name = "entrepotSource_id")
    private Entrepot entrepotSource;
    
    @ManyToOne
    @JoinColumn(name = "entrepotDestination_id")
    private Entrepot entrepotDestination;
    @ManyToOne
    @JoinColumn(name = "identreprise", insertable = false, updatable = false)
    private Entreprise entreprise;

    public enum StatutTransfert {
        EN_ATTENTE, EN_TRANSIT, RECU, ANNULE
    }
}