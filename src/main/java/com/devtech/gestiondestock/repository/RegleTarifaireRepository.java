package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.RegleTarifaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegleTarifaireRepository extends JpaRepository<RegleTarifaire, Integer> {
    RegleTarifaire findByCode(String code);
    List<RegleTarifaire> findByEntrepriseId(Integer entrepriseId);
    List<RegleTarifaire> findByCategorieId(Integer categorieId);
    List<RegleTarifaire> findByTypeRegle(RegleTarifaire.TypeRegle typeRegle);
    List<RegleTarifaire> findByEstActifTrue();
}
