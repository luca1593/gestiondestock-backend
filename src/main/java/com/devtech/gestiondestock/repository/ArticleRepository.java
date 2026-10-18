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

    /**
     * Variante tolerante aux doublons : renvoie toutes les occurrences d'un code
     * (scopee a l'entreprise par l'intercepteur multi-tenant). A utiliser pour la
     * verification d'unicite, car {@link #findArticleByCodeArticle(String)} (Optional)
     * leverait une IncorrectResultSizeDataAccessException s'il existe deja un doublon.
     */
    List<Article> findAllByCodeArticle(String codeArticle);

    List<Article> findAllByCategoryId(Integer idCategory);

    List<Article> findByDesignationContainingIgnoreCase(String designation);

    List<Article> findByCodeArticleContainingIgnoreCase(String code);

    List<Article> findByDesignationContainingIgnoreCaseOrCodeArticleContainingIgnoreCase(String designation, String code);

    @Query("SELECT a FROM Article a WHERE a.entreprise.id = :idEntreprise")
    List<Article> findAllByIdentreprise(@Param("idEntreprise") Integer idEntreprise);
}
