package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.InventaireLigne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventaireLigneRepository extends JpaRepository<InventaireLigne, Integer> {
    List<InventaireLigne> findByInventaireId(Integer inventaireId);
}
