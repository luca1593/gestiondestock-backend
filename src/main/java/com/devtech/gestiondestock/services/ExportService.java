package com.devtech.gestiondestock.services;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;

public interface ExportService {
    void exportArticlesToExcel(HttpServletResponse response, Integer identreprise);
    void exportClientsToExcel(HttpServletResponse response, Integer identreprise);
    void exportFournisseursToExcel(HttpServletResponse response, Integer identreprise);
    void exportVentesToExcel(HttpServletResponse response, Integer identreprise, Instant debut, Instant fin);
    void exportCommandesClientToExcel(HttpServletResponse response, Integer identreprise);
    void exportStockToExcel(HttpServletResponse response, Integer identreprise);
    void exportAvoirToPdf(HttpServletResponse response, Integer avoirId);
}
