package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "alerte_stock")
public class AlertStock extends AbstractEntity {
    @Column(name = "article_id")
    private Integer articleId;

    @Column(name = "designation")
    private String designation;

    @Column(name = "stock_actuel")
    private Double stockActuel;

    @Column(name = "seuil_minimum")
    private Double seuilMinimum;

    @Column(name = "seuil_critique")
    private Double seuilCritique;

    @Column(name = "niveau_alerte")
    private String niveauAlerte;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "identreprise")
    private Integer identreprise;
}
