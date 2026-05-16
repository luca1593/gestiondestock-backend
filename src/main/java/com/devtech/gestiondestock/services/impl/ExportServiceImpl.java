package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.*;
import com.devtech.gestiondestock.services.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ArticleRepository articleRepository;
    private final ClientRepository clientRepository;
    private final FournisseurRepository fournisseurRepository;
    private final VenteRepository venteRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final LigneVenteRepository ligneVenteRepository;

    @Override
    public void exportArticlesToExcel(HttpServletResponse response, Integer identreprise) {
        List<Article> articles = articleRepository.findAllByIdentreprise(identreprise);
        writeArticlesToExcel(response, articles);
    }

    @Override
    public void exportClientsToExcel(HttpServletResponse response, Integer identreprise) {
        List<Client> clients = clientRepository.findAllByIdentreprise(identreprise);
        writeClientsToExcel(response, clients);
    }

    @Override
    public void exportFournisseursToExcel(HttpServletResponse response, Integer identreprise) {
        List<Fournisseur> fournisseurs = fournisseurRepository.findAllByIdentreprise(identreprise);
        writeFournisseursToExcel(response, fournisseurs);
    }

    @Override
    public void exportVentesToExcel(HttpServletResponse response, Integer identreprise, Instant debut, Instant fin) {
        List<Vente> ventes = venteRepository.findAllByIdentreprise(identreprise).stream()
                .filter(v -> v.getDateVente() != null && !v.getDateVente().isBefore(debut) && !v.getDateVente().isAfter(fin))
                .toList();
        writeVentesToExcel(response, ventes);
    }

    @Override
    public void exportCommandesClientToExcel(HttpServletResponse response, Integer identreprise) {
        List<CommandeClient> commandes = commandeClientRepository.findAllByIdentreprise(identreprise);
        writeCommandesClientToExcel(response, commandes);
    }

    @Override
    public void exportStockToExcel(HttpServletResponse response, Integer identreprise) {
        List<Article> articles = articleRepository.findAllByIdentreprise(identreprise);
        writeStockToExcel(response, articles);
    }

    @Override
    public void exportAvoirToPdf(HttpServletResponse response, Integer avoirId) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=avoir_" + avoirId + ".pdf");
        try {
            response.getWriter().write("PDF export not yet implemented - requires iText/OpenPDF library");
        } catch (IOException e) {
            log.error("Error exporting avoir PDF", e);
        }
    }

    private void writeArticlesToExcel(HttpServletResponse response, List<Article> articles) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Articles");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Code", "Designation", "Prix HT", "TVA", "Prix TTC", "Stock", "Categorie"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Article article : articles) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(article.getId());
                row.createCell(1).setCellValue(article.getCodeArticle());
                row.createCell(2).setCellValue(article.getDesignation());
                row.createCell(3).setCellValue(article.getPrixUnitaireht() != null ? article.getPrixUnitaireht().doubleValue() : 0.0);
                row.createCell(4).setCellValue(article.getTauxTva() != null ? article.getTauxTva().doubleValue() : 0.0);
                row.createCell(5).setCellValue(article.getPrixTtc() != null ? article.getPrixTtc().doubleValue() : 0.0);
                row.createCell(6).setCellValue(article.getStock() != null ? article.getStock().doubleValue() : 0.0);
                row.createCell(7).setCellValue(article.getCategory() != null ? article.getCategory().getCode() : "N/A");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=articles.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("Error exporting articles to Excel", e);
        }
    }

    private void writeClientsToExcel(HttpServletResponse response, List<Client> clients) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clients");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Nom", "Prenom", "Email", "Telephone", "Adresse"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Client client : clients) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(client.getId());
                row.createCell(1).setCellValue(client.getNom());
                row.createCell(2).setCellValue(client.getPrenom());
                row.createCell(3).setCellValue(client.getEmail());
                row.createCell(4).setCellValue(client.getNumTel());
                row.createCell(5).setCellValue(client.getAdresse() != null ? client.getAdresse().getAdresse1() : "N/A");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=clients.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("Error exporting clients to Excel", e);
        }
    }

    private void writeFournisseursToExcel(HttpServletResponse response, List<Fournisseur> fournisseurs) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fournisseurs");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Nom", "Prenom", "Email", "Telephone"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Fournisseur f : fournisseurs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(f.getId());
                row.createCell(1).setCellValue(f.getNom());
                row.createCell(2).setCellValue(f.getPrenom());
                row.createCell(3).setCellValue(f.getEmail());
                row.createCell(4).setCellValue(f.getNumTel());
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=fournisseurs.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("Error exporting fournisseurs to Excel", e);
        }
    }

    private void writeVentesToExcel(HttpServletResponse response, List<Vente> ventes) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ventes");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Code", "Date", "Nombre articles", "Montant total"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
            for (Vente vente : ventes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(vente.getId());
                row.createCell(1).setCellValue(vente.getCode());
                row.createCell(2).setCellValue(vente.getDateVente() != null ? fmt.format(vente.getDateVente()) : "N/A");

                List<LigneVente> lignes = ligneVenteRepository.findAllByVenteId(vente.getId());
                row.createCell(3).setCellValue(lignes.size());

                double total = lignes.stream()
                        .mapToDouble(lv -> Optional.ofNullable(lv.getQuantite()).orElse(BigDecimal.ZERO)
                                .multiply(Optional.ofNullable(lv.getPrixUnitaire()).orElse(BigDecimal.ZERO)).doubleValue())
                        .sum();
                row.createCell(4).setCellValue(total);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=ventes.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("Error exporting ventes to Excel", e);
        }
    }

    private void writeCommandesClientToExcel(HttpServletResponse response, List<CommandeClient> commandes) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Commandes Client");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Code", "Date", "Etat", "Client"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
            for (CommandeClient cc : commandes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cc.getId());
                row.createCell(1).setCellValue(cc.getCode());
                row.createCell(2).setCellValue(cc.getDateCommande() != null ? fmt.format(cc.getDateCommande()) : "N/A");
                row.createCell(3).setCellValue(cc.getEtatcommande() != null ? cc.getEtatcommande().name() : "N/A");
                row.createCell(4).setCellValue(cc.getClient() != null ? cc.getClient().getNom() : "N/A");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=commandes_client.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("Error exporting commandes client to Excel", e);
        }
    }

    private void writeStockToExcel(HttpServletResponse response, List<Article> articles) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Etat du Stock");
            Row header = sheet.createRow(0);
            String[] columns = {"Code", "Designation", "Stock", "Prix Unitaire HT", "Valeur Totale", "Statut"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Article article : articles) {
                Row row = sheet.createRow(rowNum++);
                double stock = article.getStock() != null ? article.getStock().doubleValue() : 0.0;
                double prix = article.getPrixUnitaireht() != null ? article.getPrixUnitaireht().doubleValue() : 0.0;
                row.createCell(0).setCellValue(article.getCodeArticle());
                row.createCell(1).setCellValue(article.getDesignation());
                row.createCell(2).setCellValue(stock);
                row.createCell(3).setCellValue(prix);
                row.createCell(4).setCellValue(stock * prix);
                row.createCell(5).setCellValue(stock <= 0 ? "RUPTURE" : stock <= 5 ? "BAS" : stock <= 10 ? "MOYEN" : "OK");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=stock.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("Error exporting stock to Excel", e);
        }
    }
}
