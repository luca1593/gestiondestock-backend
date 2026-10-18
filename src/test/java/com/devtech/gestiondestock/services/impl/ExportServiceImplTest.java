package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.model.Adresse;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.model.Fournisseur;
import com.devtech.gestiondestock.model.LigneVente;
import com.devtech.gestiondestock.model.Vente;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.ClientRepository;
import com.devtech.gestiondestock.repository.CommandeClientRepository;
import com.devtech.gestiondestock.repository.FournisseurRepository;
import com.devtech.gestiondestock.repository.LigneVenteRepository;
import com.devtech.gestiondestock.repository.VenteRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link ExportServiceImpl}.
 *
 * <p>La generation de fichiers est testee en interceptant la reponse HTTP via
 * {@link MockHttpServletResponse} : le flux XLSX produit est relu avec POI pour
 * verifier son contenu reel (entetes + lignes). Cela couvre la logique metier
 * isolable (mapping des entites, filtrage des dates, calcul des totaux, statut
 * de stock) sans demarrer Spring.</p>
 */
@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    private static final String CONTENT_TYPE_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private FournisseurRepository fournisseurRepository;
    @Mock
    private VenteRepository venteRepository;
    @Mock
    private CommandeClientRepository commandeClientRepository;
    @Mock
    private LigneVenteRepository ligneVenteRepository;

    @InjectMocks
    private ExportServiceImpl exportService;

    private Article article(int id, String code, String designation, double stock, double prixHt) {
        Article article = new Article();
        article.setId(id);
        article.setCodeArticle(code);
        article.setDesignation(designation);
        article.setPrixUnitaireht(BigDecimal.valueOf(prixHt));
        article.setTauxTva(BigDecimal.valueOf(20));
        article.setPrixTtc(BigDecimal.valueOf(prixHt * 1.2));
        article.setStock(BigDecimal.valueOf(stock));
        Category category = new Category();
        category.setId(1);
        category.setCode("CAT-001");
        article.setCategory(category);
        return article;
    }

    private Workbook ouvrir(MockHttpServletResponse response) throws Exception {
        return new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()));
    }

    // ------------------------------------------------------------------
    // exportArticlesToExcel
    // ------------------------------------------------------------------

    @Test
    void exportArticlesToExcel_shouldWriteHeaderAndRowsAndSetHeaders() throws Exception {
        when(articleRepository.findAllByIdentreprise(1))
                .thenReturn(Arrays.asList(article(1, "ART-001", "Article test", 50, 10)));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportArticlesToExcel(response, 1);

        assertEquals(CONTENT_TYPE_XLSX, response.getContentType());
        assertTrue(response.getHeader("Content-Disposition").contains("articles.xlsx"));

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Articles");
            assertNotNull(sheet);
            assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Code", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Categorie", sheet.getRow(0).getCell(7).getStringCellValue());
            assertEquals("ART-001", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("Article test", sheet.getRow(1).getCell(2).getStringCellValue());
            assertEquals(50.0, sheet.getRow(1).getCell(6).getNumericCellValue(), 0.001);
            assertEquals("CAT-001", sheet.getRow(1).getCell(7).getStringCellValue());
        }
        verify(articleRepository, times(1)).findAllByIdentreprise(1);
    }

    @Test
    void exportArticlesToExcel_whenListeVide_shouldOnlyWriteHeader() throws Exception {
        when(articleRepository.findAllByIdentreprise(2)).thenReturn(Collections.emptyList());
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportArticlesToExcel(response, 2);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Articles");
            assertNotNull(sheet);
            // Seule la ligne d'entete (index 0) existe
            assertEquals(0, sheet.getLastRowNum());
        }
    }

    // ------------------------------------------------------------------
    // exportClientsToExcel
    // ------------------------------------------------------------------

    @Test
    void exportClientsToExcel_shouldWriteRows() throws Exception {
        Client client = new Client();
        client.setId(1);
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean@test.com");
        client.setNumTel("0601020304");
        Adresse adresse = new Adresse();
        adresse.setAdresse1("1 rue de la Paix");
        client.setAdresse(adresse);

        when(clientRepository.findAllByIdentreprise(1)).thenReturn(Arrays.asList(client));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportClientsToExcel(response, 1);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Clients");
            assertNotNull(sheet);
            assertEquals("Nom", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Dupont", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("1 rue de la Paix", sheet.getRow(1).getCell(5).getStringCellValue());
        }
        verify(clientRepository).findAllByIdentreprise(1);
    }

    @Test
    void exportClientsToExcel_whenAdresseNull_shouldWriteNA() throws Exception {
        Client client = new Client();
        client.setId(1);
        client.setNom("SansAdresse");
        client.setPrenom("Paul");
        client.setEmail("p@test.com");
        client.setNumTel("0102030405");

        when(clientRepository.findAllByIdentreprise(1)).thenReturn(Arrays.asList(client));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportClientsToExcel(response, 1);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Clients");
            assertEquals("N/A", sheet.getRow(1).getCell(5).getStringCellValue());
        }
    }

    // ------------------------------------------------------------------
    // exportFournisseursToExcel
    // ------------------------------------------------------------------

    @Test
    void exportFournisseursToExcel_shouldWriteRows() throws Exception {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(1);
        fournisseur.setNom("Transporteur");
        fournisseur.setPrenom("Paul");
        fournisseur.setEmail("contact@transporteur.com");
        fournisseur.setNumTel("0102030405");

        when(fournisseurRepository.findAllByIdentreprise(1)).thenReturn(Arrays.asList(fournisseur));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportFournisseursToExcel(response, 1);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Fournisseurs");
            assertNotNull(sheet);
            assertEquals("Transporteur", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("contact@transporteur.com", sheet.getRow(1).getCell(3).getStringCellValue());
        }
        verify(fournisseurRepository).findAllByIdentreprise(1);
    }

    // ------------------------------------------------------------------
    // exportVentesToExcel
    // ------------------------------------------------------------------

    @Test
    void exportVentesToExcel_shouldFiltrerParDateEtCalculerLeTotal() throws Exception {
        Instant debut = Instant.parse("2026-01-01T00:00:00Z");
        Instant fin = Instant.parse("2026-12-31T23:59:59Z");

        Vente dansPeriode = new Vente();
        dansPeriode.setId(1);
        dansPeriode.setCode("V-1");
        dansPeriode.setDateVente(Instant.parse("2026-06-01T10:00:00Z"));

        Vente tropTot = new Vente();
        tropTot.setId(2);
        tropTot.setCode("V-2");
        tropTot.setDateVente(Instant.parse("2025-06-01T10:00:00Z"));

        Vente tropTard = new Vente();
        tropTard.setId(3);
        tropTard.setCode("V-3");
        tropTard.setDateVente(Instant.parse("2027-06-01T10:00:00Z"));

        Vente sansDate = new Vente();
        sansDate.setId(4);
        sansDate.setCode("V-4");
        sansDate.setDateVente(null);

        when(venteRepository.findAllByIdentreprise(1))
                .thenReturn(Arrays.asList(dansPeriode, tropTot, tropTard, sansDate));

        LigneVente lv1 = new LigneVente();
        lv1.setQuantite(BigDecimal.valueOf(2));
        lv1.setPrixUnitaire(BigDecimal.valueOf(10));
        LigneVente lv2 = new LigneVente();
        lv2.setQuantite(BigDecimal.valueOf(1));
        lv2.setPrixUnitaire(BigDecimal.valueOf(5));
        when(ligneVenteRepository.findAllByVenteId(1)).thenReturn(Arrays.asList(lv1, lv2));

        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportVentesToExcel(response, 1, debut, fin);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Ventes");
            assertNotNull(sheet);
            // 1 entete + 1 seule vente dans la periode
            assertEquals(1, sheet.getLastRowNum());
            assertEquals("V-1", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals(2.0, sheet.getRow(1).getCell(3).getNumericCellValue(), 0.001);
            assertEquals(25.0, sheet.getRow(1).getCell(4).getNumericCellValue(), 0.001);
        }

        verify(ligneVenteRepository, times(1)).findAllByVenteId(1);
        verify(ligneVenteRepository, never()).findAllByVenteId(2);
        verify(ligneVenteRepository, never()).findAllByVenteId(3);
        verify(ligneVenteRepository, never()).findAllByVenteId(4);
    }

    @Test
    void exportVentesToExcel_whenAucuneVenteDansPeriode_shouldOnlyWriteHeader() throws Exception {
        Instant debut = Instant.parse("2026-01-01T00:00:00Z");
        Instant fin = Instant.parse("2026-12-31T23:59:59Z");

        Vente tropTot = new Vente();
        tropTot.setId(2);
        tropTot.setCode("V-2");
        tropTot.setDateVente(Instant.parse("2020-01-01T00:00:00Z"));

        when(venteRepository.findAllByIdentreprise(1)).thenReturn(Arrays.asList(tropTot));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportVentesToExcel(response, 1, debut, fin);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Ventes");
            assertEquals(0, sheet.getLastRowNum());
        }
        verifyNoInteractions(ligneVenteRepository);
    }

    // ------------------------------------------------------------------
    // exportCommandesClientToExcel
    // ------------------------------------------------------------------

    @Test
    void exportCommandesClientToExcel_shouldWriteRows() throws Exception {
        Client client = new Client();
        client.setId(1);
        client.setNom("Dupont");

        CommandeClient commande = new CommandeClient();
        commande.setId(1);
        commande.setCode("CC-1");
        commande.setDateCommande(Instant.parse("2026-06-01T10:00:00Z"));
        commande.setEtatcommande(EtatCommande.LIVREE);
        commande.setClient(client);

        when(commandeClientRepository.findAllByIdentreprise(1)).thenReturn(Arrays.asList(commande));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportCommandesClientToExcel(response, 1);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Commandes Client");
            assertNotNull(sheet);
            assertEquals("CC-1", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("LIVREE", sheet.getRow(1).getCell(3).getStringCellValue());
            assertEquals("Dupont", sheet.getRow(1).getCell(4).getStringCellValue());
        }
        verify(commandeClientRepository).findAllByIdentreprise(1);
    }

    // ------------------------------------------------------------------
    // exportStockToExcel
    // ------------------------------------------------------------------

    @Test
    void exportStockToExcel_shouldCalculerValeurEtStatut() throws Exception {
        when(articleRepository.findAllByIdentreprise(1)).thenReturn(Arrays.asList(
                article(1, "ART-RUPTURE", "Rupture", 0, 10),
                article(2, "ART-BAS", "Stock bas", 3, 10),
                article(3, "ART-MOYEN", "Stock moyen", 8, 10),
                article(4, "ART-OK", "Stock ok", 50, 10)));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportStockToExcel(response, 1);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Etat du Stock");
            assertNotNull(sheet);
            assertEquals("RUPTURE", sheet.getRow(1).getCell(5).getStringCellValue());
            assertEquals("BAS", sheet.getRow(2).getCell(5).getStringCellValue());
            assertEquals("MOYEN", sheet.getRow(3).getCell(5).getStringCellValue());
            assertEquals("OK", sheet.getRow(4).getCell(5).getStringCellValue());
            // Valeur totale = stock * prix HT
            assertEquals(30.0, sheet.getRow(2).getCell(4).getNumericCellValue(), 0.001);
            assertEquals(500.0, sheet.getRow(4).getCell(4).getNumericCellValue(), 0.001);
        }
    }

    @Test
    void exportStockToExcel_whenValeursNull_shouldUtiliserZero() throws Exception {
        Article article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-NULL");
        article.setDesignation("Article sans stock");
        article.setStock(null);
        article.setPrixUnitaireht(null);
        when(articleRepository.findAllByIdentreprise(1)).thenReturn(Arrays.asList(article));
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportStockToExcel(response, 1);

        try (Workbook wb = ouvrir(response)) {
            Sheet sheet = wb.getSheet("Etat du Stock");
            assertEquals(0.0, sheet.getRow(1).getCell(2).getNumericCellValue(), 0.001);
            assertEquals(0.0, sheet.getRow(1).getCell(4).getNumericCellValue(), 0.001);
            assertEquals("RUPTURE", sheet.getRow(1).getCell(5).getStringCellValue());
        }
    }

    // ------------------------------------------------------------------
    // exportAvoirToPdf
    // ------------------------------------------------------------------

    @Test
    void exportAvoirToPdf_shouldSetPdfHeadersAndPlaceholderContent() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        exportService.exportAvoirToPdf(response, 42);

        assertEquals("application/pdf", response.getContentType());
        assertEquals("attachment; filename=avoir_42.pdf", response.getHeader("Content-Disposition"));
        assertTrue(response.getContentAsString().contains("PDF export not yet implemented"));
    }
}
