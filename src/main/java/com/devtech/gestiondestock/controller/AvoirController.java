package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.dto.AvoirDto;
import com.devtech.gestiondestock.services.AvoirService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @PathVariable Integer clientId,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        return ResponseEntity.ok(avoirService.findByClientId(clientId, identreprise));
    }

    @GetMapping("/vente/{venteId}")
    @Operation(summary = "Avoirs d'une vente")
    public ResponseEntity<List<AvoirDto>> findByVente(
            @PathVariable Integer venteId,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        return ResponseEntity.ok(avoirService.findByVenteId(venteId, identreprise));
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Avoirs par etat (EN_ATTENTE, VALIDE, ANNULE)")
    public ResponseEntity<List<AvoirDto>> findByEtat(
            @PathVariable String etat,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        return ResponseEntity.ok(avoirService.findByEtat(etat, identreprise));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Avoirs par periode")
    public ResponseEntity<List<AvoirDto>> findByDateRange(
            @RequestParam Instant debut,
            @RequestParam Instant fin,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        return ResponseEntity.ok(avoirService.findByDateRange(debut, fin, identreprise));
    }

    @GetMapping("/all")
    @Operation(summary = "Tous les avoirs")
    public ResponseEntity<List<AvoirDto>> findAll(
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        return ResponseEntity.ok(avoirService.findAll(identreprise));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer un avoir")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        avoirService.delete(id);
        return ResponseEntity.ok().build();
    }
}
