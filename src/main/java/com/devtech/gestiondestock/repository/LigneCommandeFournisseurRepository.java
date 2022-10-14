package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.LigneCommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneCommandeFournisseurRepository extends JpaRepository<LigneCommandeFournisseur, Integer> {
    List<LigneCommandeFournisseur> findAllByCommandeFournisseurId(Integer idCommandeFournisseur);
    List<LigneCommandeFournisseur> findAllByArticleId(Integer idArticle);
}
