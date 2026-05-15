package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {
    Lot findByNumeroLot(String numeroLot);
    List<Lot> findByArticleId(Integer articleId);
    List<Lot> findByEntrepotId(Integer entrepotId);
    List<Lot> findByDateExpirationBefore(LocalDateTime date);
    List<Lot> findByEstCompletementUtiliseFalse();
}
