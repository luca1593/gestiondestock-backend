package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.dto.AvoirDto;
import com.devtech.gestiondestock.services.AvoirService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

@RestController
@AllArgsConstructor
@Tag(name = "Avoirs", description = "Gestion des retours et avoirs")
@RequestMapping(APP_ROOT + "/avoirs")
public class AvoirController {

    private final AvoirService avoirService;

    private Integer getCurrentEntrepriseId() {
        String idEntreprise = MDC.get("idEntreprise");
        if (idEntreprise == null) {
            throw new IllegalStateException("idEntreprise not set in MDC - user not authenticated");
        }
        return Integer.parseInt(idEntreprise);
    }

    @PostMapping("/save")
    @Operation(summary = "Creer/modifier un avoir")
    public ResponseEntity<AvoirDto> save(@RequestBody AvoirDto dto) {
        return ResponseEntity.ok(avoirService.save(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un avoir par ID")
    public ResponseEntity<AvoirDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(avoirService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Avoirs d'un client")
    public ResponseEntity<List<AvoirDto>> findByClient(
            @PathVariable Integer clientId) {
        return ResponseEntity.ok(avoirService.findByClientId(clientId, getCurrentEntrepriseId()));
    }

    @GetMapping("/vente/{venteId}")
    @Operation(summary = "Avoirs d'une vente")
    public ResponseEntity<List<AvoirDto>> findByVente(
            @PathVariable Integer venteId) {
        return ResponseEntity.ok(avoirService.findByVenteId(venteId, getCurrentEntrepriseId()));
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Avoirs par etat (EN_ATTENTE, VALIDE, ANNULE)")
    public ResponseEntity<List<AvoirDto>> findByEtat(
            @PathVariable String etat) {
        return ResponseEntity.ok(avoirService.findByEtat(etat, getCurrentEntrepriseId()));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Avoirs par periode")
    public ResponseEntity<List<AvoirDto>> findByDateRange(
            @RequestParam Instant debut,
            @RequestParam Instant fin) {
        return ResponseEntity.ok(avoirService.findByDateRange(debut, fin, getCurrentEntrepriseId()));
    }

    @GetMapping("/all")
    @Operation(summary = "Tous les avoirs")
    public ResponseEntity<List<AvoirDto>> findAll() {
        return ResponseEntity.ok(avoirService.findAll(getCurrentEntrepriseId()));
    }

    @PutMapping("/etat/{id}")
    @Operation(summary = "Mettre a jour l'etat d'un avoir (EN_ATTENTE, VALIDE, ANNULE)")
    public ResponseEntity<AvoirDto> updateEtat(@PathVariable Integer id, @RequestParam String etat) {
        return ResponseEntity.ok(avoirService.updateEtat(id, etat));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer un avoir")
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        avoirService.delete(id);
        return ResponseEntity.ok().build();
    }
}
