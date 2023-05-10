package com.devtech.gestiondestock.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "commmandClient")
public class CommandeClient extends AbstractEntity{
    @Column(name = "code")
    private String code;
    @Column(name = "datecommande")
    private Instant dateCommande;
    @Column(name = "etatcommande")
    private EtatCommande etatcommande;
    @ManyToOne
    @JoinColumn(name = "idclient")
    Client client;
    @OneToMany(mappedBy = "commandeClient")
    private List<LigneCommandeClient> ligneCommandeClients;
    @Column(name = "identrepeiae")
    private Integer identreprise;
}
