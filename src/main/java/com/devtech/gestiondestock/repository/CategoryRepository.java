package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findCategoryByCode(String code);/*

    Optional<Category> findCategoryByIdAmdIdEntreprise(Integer id, Integer idEntreprise);
*/
}
