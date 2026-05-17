package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "avoir")
public class Avoir extends AbstractEntity {
    @Column(name = "code")
    private String code;

    @Column(name = "date_avoir")
    private Instant dateAvoir;

    @Column(name = "montant")
    private Double montant;

    @Column(name = "raison")
    private String raison;

    @Column(name = "etat")
    private String etat;

    @ManyToOne
    @JoinColumn(name = "idclient")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "idvente")
    private Vente vente;


}
