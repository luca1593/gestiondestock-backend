package com.devtech.gestiondestock.repository;

import com.devtech.gestiondestock.model.LigneCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LigneCommandeClientRepository extends JpaRepository<LigneCommandeClient, Integer> {
    List<LigneCommandeClient> findAllByCommandeClientId(Integer idCommandeClient);
    List<LigneCommandeClient> findAllByArticleId(Integer idArticle);
    @Query("SELECT lcc FROM LigneCommandeClient lcc WHERE lcc.identreprise = :identreprise")
    List<LigneCommandeClient> findAllByIdentreprise(@Param("identreprise") Integer identreprise);
}
