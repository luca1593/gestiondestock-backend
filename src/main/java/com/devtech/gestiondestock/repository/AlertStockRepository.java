package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.AlertStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertStockRepository extends JpaRepository<AlertStock, Integer> {
    List<AlertStock> findByIdentrepriseAndActiveTrue(Integer identreprise);
    List<AlertStock> findByNiveauAlerte(String niveauAlerte);

    @Query("SELECT a FROM AlertStock a WHERE a.articleId = :articleId AND a.active = true")
    List<AlertStock> findByArticleIdAndActive(@Param("articleId") Integer articleId);
}
