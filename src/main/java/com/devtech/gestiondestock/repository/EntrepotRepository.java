package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntrepotRepository extends JpaRepository<Entrepot, Integer> {
    List<Entrepot> findByEntrepriseId(Integer entrepriseId);
    Entrepot findByCode(String code);
}