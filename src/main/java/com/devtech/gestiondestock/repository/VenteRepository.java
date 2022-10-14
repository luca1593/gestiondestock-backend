package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Vente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VenteRepository extends JpaRepository<Vente, Integer> {
    Optional<Vente> findVenteByCode(String code);
    List<Vente> findVenteByDateVente(Instant dateVente);
}
