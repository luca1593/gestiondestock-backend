package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findCategoryByCode(String code);

    /**
     * Variante tolerante aux doublons : renvoie toutes les occurrences d'un code
     * (scopee a l'entreprise par l'intercepteur multi-tenant). A utiliser pour la
     * verification d'unicite, car {@link #findCategoryByCode(String)} (Optional)
     * leverait une IncorrectResultSizeDataAccessException s'il existe deja un doublon.
     */
    List<Category> findAllByCode(String code);/*

    Optional<Category> findCategoryByIdAmdIdEntreprise(Integer id, Integer idEntreprise);
*/
}
