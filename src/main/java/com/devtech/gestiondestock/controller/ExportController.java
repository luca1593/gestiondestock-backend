package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.services.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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

    @GetMapping("/excel/articles")
    @Operation(summary = "Exporter les articles en Excel")
    public void exportArticles(HttpServletResponse response,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        exportService.exportArticlesToExcel(response, identreprise);
    }

    @GetMapping("/excel/clients")
    @Operation(summary = "Exporter les clients en Excel")
    public void exportClients(HttpServletResponse response,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        exportService.exportClientsToExcel(response, identreprise);
    }

    @GetMapping("/excel/fournisseurs")
    @Operation(summary = "Exporter les fournisseurs en Excel")
    public void exportFournisseurs(HttpServletResponse response,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        exportService.exportFournisseursToExcel(response, identreprise);
    }

    @GetMapping("/excel/ventes")
    @Operation(summary = "Exporter les ventes en Excel")
    public void exportVentes(HttpServletResponse response,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise,
            @RequestParam Instant debut,
            @RequestParam Instant fin) {
        exportService.exportVentesToExcel(response, identreprise, debut, fin);
    }

    @GetMapping("/excel/commandes-client")
    @Operation(summary = "Exporter les commandes client en Excel")
    public void exportCommandesClient(HttpServletResponse response,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        exportService.exportCommandesClientToExcel(response, identreprise);
    }

    @GetMapping("/excel/stock")
    @Operation(summary = "Exporter l'état du stock en Excel")
    public void exportStock(HttpServletResponse response,
            @RequestHeader(value = "X-Id-Entreprise", required = false, defaultValue = "1") Integer identreprise) {
        exportService.exportStockToExcel(response, identreprise);
    }

    @GetMapping("/pdf/avoir/{id}")
    @Operation(summary = "Exporter un avoir en PDF")
    public void exportAvoirPdf(HttpServletResponse response, @PathVariable Integer id) {
        exportService.exportAvoirToPdf(response, id);
    }
}
