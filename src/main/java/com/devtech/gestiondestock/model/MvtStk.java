package com.devtech.gestiondestock.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mvtStk")
public class MvtStk extends AbstractEntity{
    @Column(name = "dateMvt")
    private Instant dateMvt;
    @Column(name = "quantite")
    private BigDecimal quantite;
    @ManyToOne
    @JoinColumn(name = "idarticle")
    private Article article;
    @Column(name = "typemvt")
    private TypeMvt typeMvt;
    @Column(name = "sourcemvtstk")
    private SourceMvtStk sourceMvt;
    @Column(name = "identreprise")
    private Integer identreprise;
}
