package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author luca
 */
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Optional<Article> findArticleByCodeArticle(String code);

    List<Article> findAllByCategoryId(Integer idCategory);

    List<Article> findByDesignationContainingIgnoreCase(String designation);

    List<Article> findByCodeArticleContainingIgnoreCase(String code);

    List<Article> findByDesignationContainingIgnoreCaseOrCodeArticleContainingIgnoreCase(String designation, String code);

    @Query("SELECT a FROM Article a WHERE a.entreprise.id = :idEntreprise OR a.entreprise IS NULL")
    List<Article> findAllByEntreprise(@Param("idEntreprise") Integer idEntreprise);
}
