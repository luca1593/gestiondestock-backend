package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.LigneVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LigneVenteRepository extends JpaRepository<LigneVente, Integer> {
    List<LigneVente> findAllByArticleId(Integer idArticle);
    List<LigneVente> findAllByVenteId(Integer idVente);
    @Query("SELECT lv FROM LigneVente lv WHERE lv.identreprise = :identreprise")
    List<LigneVente> findAllByIdentreprise(@Param("identreprise") Integer identreprise);
}
