package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.CommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.model.EtatCommande;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface CommandeClientService {

    CommandeClientDto save(CommandeClientDto dto);
    CommandeClientDto findById(Integer id);
    CommandeClientDto updateEtatCommande(Integer id, EtatCommande etatCommande);
    CommandeClientDto updateQuantiterCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite);
    CommandeClientDto updateClient(Integer idCommande, Integer idClient);
    CommandeClientDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle);
    CommandeClientDto deleteArticle(Integer idCommande, Integer idLigneCommande);
    List<LigneCommandeClientDto> findAllByCommandeClient(Integer idCommande);
    CommandeClientDto findByCodeCommande(String code);
    List<CommandeClientDto> findByDateCommande(Instant dateCommande);
    List<CommandeClientDto> findAll();
    List<CommandeClientDto> findAllByClientDto(ClientDto clientDto);
    void delete(Integer id);

}
