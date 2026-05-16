package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.services.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

@RestController
@AllArgsConstructor
@Tag(name = "Export", description = "Export de données en Excel/PDF")
@RequestMapping(APP_ROOT + "/export")
public class ExportController {

    private final ExportService exportService;

    private Integer getCurrentEntrepriseId() {
        String idEntreprise = MDC.get("idEntreprise");
        if (idEntreprise == null) {
            throw new IllegalStateException("idEntreprise not set in MDC - user not authenticated");
        }
        return Integer.parseInt(idEntreprise);
    }

    @GetMapping("/excel/articles")
    @Operation(summary = "Exporter les articles en Excel")
    public void exportArticles(HttpServletResponse response) {
        exportService.exportArticlesToExcel(response, getCurrentEntrepriseId());
    }

    @GetMapping("/excel/clients")
    @Operation(summary = "Exporter les clients en Excel")
    public void exportClients(HttpServletResponse response) {
        exportService.exportClientsToExcel(response, getCurrentEntrepriseId());
    }

    @GetMapping("/excel/fournisseurs")
    @Operation(summary = "Exporter les fournisseurs en Excel")
    public void exportFournisseurs(HttpServletResponse response) {
        exportService.exportFournisseursToExcel(response, getCurrentEntrepriseId());
    }

    @GetMapping("/excel/ventes")
    @Operation(summary = "Exporter les ventes en Excel")
    public void exportVentes(HttpServletResponse response,
            @RequestParam Instant debut,
            @RequestParam Instant fin) {
        exportService.exportVentesToExcel(response, getCurrentEntrepriseId(), debut, fin);
    }

    @GetMapping("/excel/commandes-client")
    @Operation(summary = "Exporter les commandes client en Excel")
    public void exportCommandesClient(HttpServletResponse response) {
        exportService.exportCommandesClientToExcel(response, getCurrentEntrepriseId());
    }

    @GetMapping("/excel/stock")
    @Operation(summary = "Exporter l'état du stock en Excel")
    public void exportStock(HttpServletResponse response) {
        exportService.exportStockToExcel(response, getCurrentEntrepriseId());
    }

    @GetMapping("/pdf/avoir/{id}")
    @Operation(summary = "Exporter un avoir en PDF")
    public void exportAvoirPdf(HttpServletResponse response, @PathVariable Integer id) {
        exportService.exportAvoirToPdf(response, id);
    }
}
