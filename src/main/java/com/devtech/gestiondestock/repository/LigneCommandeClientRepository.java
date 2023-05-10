package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.LigneCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneCommandeClientRepository extends JpaRepository<LigneCommandeClient, Integer> {
    List<LigneCommandeClient> findAllByCommandeClientId(Integer idCommandeClient);
    List<LigneCommandeClient> findAllByArticleId(Integer idArticle);
}
