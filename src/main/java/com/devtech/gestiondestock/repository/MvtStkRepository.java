package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.MvtStk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface MvtStkRepository extends JpaRepository<MvtStk, Integer> {
    List<MvtStk> findMvtStkByDateMvt(Instant dateMvt);
    List<MvtStk> findMvtStkByTypeMvt(String typeMvt);
    @Query("select sum(m.quantite) where MvtStk m.article.id =: idArticle")
    BigDecimal stockReelArticle(@Param("idArticle") Integer idArticle);
    List<MvtStk> findAllByArticleId(Integer idArticle);
}
