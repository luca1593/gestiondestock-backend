package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.LigneVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneVenteRepository extends JpaRepository<LigneVente, Integer> {
    List<LigneVente> findAllByArticleId(Integer idArticle);
    List<LigneVente> findAllByVenteId(Integer idVente);
}
