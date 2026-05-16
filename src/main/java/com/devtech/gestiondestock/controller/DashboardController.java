package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.dto.ArticleStatsDto;
import com.devtech.gestiondestock.dto.DashboardStatsDto;
import com.devtech.gestiondestock.dto.VenteStatsDto;
import com.devtech.gestiondestock.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

@RestController
@AllArgsConstructor
@Tag(name = "Dashboard", description = "Statistiques et tableaux de bord")
@RequestMapping(APP_ROOT + "/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    private Integer getCurrentEntrepriseId() {
        String idEntreprise = MDC.get("idEntreprise");
        if (idEntreprise == null) {
            throw new IllegalStateException("idEntreprise not set in MDC - user not authenticated");
        }
        return Integer.parseInt(idEntreprise);
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales", description = "Retourne les statistiques globales de l'entreprise")
    public ResponseEntity<DashboardStatsDto> getGlobalStats() {
        return ResponseEntity.ok(dashboardService.getGlobalStats(getCurrentEntrepriseId()));
    }

    @GetMapping("/articles/top")
    @Operation(summary = "Top articles", description = "Retourne les articles les plus vendus")
    public ResponseEntity<List<ArticleStatsDto>> getTopArticles(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getTopArticles(getCurrentEntrepriseId(), limit));
    }

    @GetMapping("/ventes/periode")
    @Operation(summary = "Ventes par période", description = "Retourne les statistiques de ventes sur une période")
    public ResponseEntity<List<VenteStatsDto>> getVentesParPeriode(
            @RequestParam Instant debut,
            @RequestParam Instant fin) {
        return ResponseEntity.ok(dashboardService.getVentesParPeriode(getCurrentEntrepriseId(), debut, fin));
    }

    @GetMapping("/chiffre-affaires/mois")
    @Operation(summary = "Chiffre d'affaires mensuel", description = "Retourne le chiffre d'affaires par mois pour une année")
    public ResponseEntity<List<VenteStatsDto>> getChiffreAffairesParMois(
            @RequestParam(defaultValue = "2026") Integer annee) {
        return ResponseEntity.ok(dashboardService.getChiffreAffairesParMois(getCurrentEntrepriseId(), annee));
    }
}
