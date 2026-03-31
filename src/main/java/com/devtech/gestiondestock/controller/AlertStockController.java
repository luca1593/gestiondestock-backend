package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.dto.AlertStockDto;
import com.devtech.gestiondestock.services.AlertStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

@RestController
@AllArgsConstructor
@Tag(name = "Alertes Stock", description = "Gestion des alertes de stock")
@RequestMapping(APP_ROOT + "/alert-stock")
public class AlertStockController {

    private final AlertStockService alertStockService;

    @PostMapping("/save")
    @Operation(summary = "Creer/modifier une alerte")
    public ResponseEntity<AlertStockDto> save(@RequestBody AlertStockDto dto) {
        return ResponseEntity.ok(alertStockService.save(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver une alerte par ID")
    public ResponseEntity<AlertStockDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(alertStockService.findById(id));
    }

    @GetMapping("/entreprise/{identreprise}")
    @Operation(summary = "Toutes les alertes d'une entreprise")
    public ResponseEntity<List<AlertStockDto>> findAllByEntreprise(@PathVariable Integer identreprise) {
        return ResponseEntity.ok(alertStockService.findAllByEntreprise(identreprise));
    }

    @GetMapping("/actives/{identreprise}")
    @Operation(summary = "Alertes actives avec verification automatique")
    public ResponseEntity<List<AlertStockDto>> findAlertesActives(@PathVariable Integer identreprise) {
        return ResponseEntity.ok(alertStockService.findAlertesActives(identreprise));
    }

    @GetMapping("/niveau/{niveau}")
    @Operation(summary = "Alertes par niveau (CRITIQUE, BAS, MOYEN)")
    public ResponseEntity<List<AlertStockDto>> findByNiveau(@PathVariable String niveau) {
        return ResponseEntity.ok(alertStockService.findByNiveauAlerte(niveau));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer une alerte")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertStockService.delete(id);
        return ResponseEntity.ok().build();
    }
}
