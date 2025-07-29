package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.CommandeClientApi;
import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.CommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.services.CommandeClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * @author luca
 */
@RestController
public class CommandeClientController implements CommandeClientApi {

    private CommandeClientService commandeClientService;

    @Autowired
    public CommandeClientController(CommandeClientService commandeClientService) {
        this.commandeClientService = commandeClientService;
    }

    @Override
    public ResponseEntity<CommandeClientDto> save(CommandeClientDto dto, Long dateCommandeClient) {
        Instant dateCmd = Instant.ofEpochMilli(dateCommandeClient);
        dto.setDateCommande(dateCmd);
        return ResponseEntity.ok(this.commandeClientService.save(dto));
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateEtatCommande(Integer id, EtatCommande etatCommande) {
        return ResponseEntity.ok(this.commandeClientService.updateEtatCommande(id, etatCommande));
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateQuantiterCommande(Integer idCommande,
            Integer idLigneCommande,
            BigDecimal quantite) {
        return ResponseEntity.ok(
                this.commandeClientService.updateQuantiterCommande(idCommande, idLigneCommande, quantite));
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateClient(Integer idCommande, Integer idClient) {
        return ResponseEntity.ok(this.commandeClientService.updateClient(idCommande, idClient));
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateArticle(Integer idCommande,
            Integer idLigneCommande,
            Integer newIdArticle) {
        return ResponseEntity.ok(
                this.commandeClientService.updateArticle(idCommande, idLigneCommande, newIdArticle));
    }

    @Override
    public ResponseEntity<CommandeClientDto> deleteArticle(Integer idCommande, Integer idLigneCommande) {
        return ResponseEntity.ok(this.commandeClientService.deleteArticle(idCommande, idLigneCommande));
    }

    @Override
    public ResponseEntity<List<LigneCommandeClientDto>> findAllByCommandeClient(Integer idCommande) {
        return ResponseEntity.ok(this.commandeClientService.findAllByCommandeClient(idCommande));
    }

    @Override
    public ResponseEntity<CommandeClientDto> findById(Integer id) {
        return ResponseEntity.ok(this.commandeClientService.findById(id));
    }

    @Override
    public ResponseEntity<CommandeClientDto> findByCodeCommande(String code) {
        return ResponseEntity.ok(this.commandeClientService.findByCodeCommande(code));
    }

    @Override
    public ResponseEntity<List<CommandeClientDto>> findByDateCommande(Instant dateCommande) {
        return ResponseEntity.ok(this.commandeClientService.findByDateCommande(dateCommande));
    }

    @Override
    public ResponseEntity<List<CommandeClientDto>> findAll() {
        return ResponseEntity.ok(this.commandeClientService.findAll());
    }

    @Override
    public ResponseEntity<List<CommandeClientDto>> findAllByClient(ClientDto dto) {
        return ResponseEntity.ok(this.commandeClientService.findAllByClientDto(dto));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity delete(Integer id) {
        this.commandeClientService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<LigneCommandeClientDto>> findAllLigneCommandeClient(Integer icCommade) {
        return ResponseEntity.ok(this.commandeClientService.findAllByCommandeClient(icCommade));
    }
}
