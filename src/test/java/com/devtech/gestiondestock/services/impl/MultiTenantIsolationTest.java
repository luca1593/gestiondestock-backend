package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.*;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.services.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MultiTenantIsolationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private EntrepriseService entrepriseService;
    @Autowired private UtilisateurService utilisateurService;
    @Autowired private ArticleService articleService;
    @Autowired private CategoryService categoryService;
    @Autowired private ClientService clientService;
    @Autowired private FactureService factureService;
    @Autowired private EntrepotService entrepotService;

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private Integer entAId = 1;
    private Integer entBId;
    private Integer entCId;

    @BeforeAll
    void setUpOnce() {
        MDC.put("idEntreprise", "1");

        EntrepriseDto entB = entrepriseService.save(EntrepriseDto.builder()
                .nom("Isolation Enterprise B")
                .description("Enterprise B for isolation testing")
                .email("isolation-b@test.com")
                .codeFiscal("ISO-B")
                .numTel("0100000001")
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue B")
                        .ville("Paris").codePostal("75001").pays("France")
                        .build())
                .build());
        entBId = entB.getId();

        EntrepriseDto entC = entrepriseService.save(EntrepriseDto.builder()
                .nom("Isolation Enterprise C")
                .description("Enterprise C for isolation testing")
                .email("isolation-c@test.com")
                .codeFiscal("ISO-C")
                .numTel("0100000002")
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue C")
                        .ville("Lyon").codePostal("69001").pays("France")
                        .build())
                .build());
        entCId = entC.getId();

        utilisateurService.save(UtilisateurDto.builder()
                .nom("Admin B")
                .prenom("User")
                .email("admin-b@test.com")
                .motDePasse("admin123")
                .dateDeNaissance(Instant.now())
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue B")
                        .ville("Paris").codePostal("75001").pays("France")
                        .build())
                .entreprise(EntrepriseDto.builder().id(entBId).build())
                .build());

        utilisateurService.save(UtilisateurDto.builder()
                .nom("Admin C")
                .prenom("User")
                .email("admin-c@test.com")
                .motDePasse("admin123")
                .dateDeNaissance(Instant.now())
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue C")
                        .ville("Lyon").codePostal("69001").pays("France")
                        .build())
                .entreprise(EntrepriseDto.builder().id(entCId).build())
                .build());
    }

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");
    }

    @AfterAll
    void tearDownOnce() {
        MDC.clear();
    }

    // ───── Helpers ─────

    private CategoryDto createCategoryFor(Integer entrepriseId, String code) {
        MDC.put("idEntreprise", entrepriseId.toString());
        CategoryDto dto = categoryService.save(CategoryDto.builder()
                .code(code)
                .designation("Cat " + code)
                .identreprise(entrepriseId)
                .build());
        flushAndClear();
        return dto;
    }

    private ArticleDto createArticleFor(Integer entrepriseId, String code, Integer categoryId) {
        MDC.put("idEntreprise", entrepriseId.toString());
        ArticleDto dto = articleService.save(ArticleDto.builder()
                .codeArticle(code)
                .designation("Article " + code)
                .prixUnitaireht(BigDecimal.valueOf(100))
                .tauxTva(BigDecimal.valueOf(20))
                .prixTtc(BigDecimal.valueOf(120))
                .stock(BigDecimal.valueOf(50))
                .category(CategoryDto.builder().id(categoryId).build())
                .entreprise(EntrepriseDto.builder().id(entrepriseId).build())
                .build());
        flushAndClear();
        return dto;
    }

    private ClientDto createClientFor(Integer entrepriseId, String email) {
        MDC.put("idEntreprise", entrepriseId.toString());
        ClientDto dto = clientService.save(ClientDto.builder()
                .nom("Client " + entrepriseId)
                .prenom("Test")
                .email(email)
                .numTel("0100000000")
                .identreprise(entrepriseId)
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue Test")
                        .ville("Paris").codePostal("75001").pays("France")
                        .build())
                .build());
        flushAndClear();
        return dto;
    }

    private FactureDto createFactureFor(Integer entrepriseId, String numero, Integer clientId) {
        MDC.put("idEntreprise", entrepriseId.toString());
        FactureDto dto = factureService.save(FactureDto.builder()
                .numeroFacture(numero)
                .dateFacture(LocalDateTime.now())
                .statut(Facture.StatutFacture.EN_ATTENTE)
                .montantHT(BigDecimal.valueOf(500))
                .montantTVA(BigDecimal.valueOf(100))
                .montantTTC(BigDecimal.valueOf(600))
                .montantPaye(BigDecimal.ZERO)
                .montantRestant(BigDecimal.valueOf(600))
                .clientId(clientId)
                .entrepriseId(entrepriseId)
                .build());
        flushAndClear();
        return dto;
    }

    // ───── UTILISATEUR ─────

    @Test
    void testUtilisateurFindAll_EachEnterpriseSeesOnlyOwnUsers() {
        MDC.put("idEntreprise", entAId.toString());
        List<UtilisateurDto> usersA = utilisateurService.findAll();
        assertFalse(usersA.isEmpty());
        for (UtilisateurDto u : usersA) {
            assertEquals(entAId, u.getEntreprise().getId());
            assertNull(u.getMotDePasse());
        }

        MDC.put("idEntreprise", entBId.toString());
        List<UtilisateurDto> usersB = utilisateurService.findAll();
        assertFalse(usersB.isEmpty());
        for (UtilisateurDto u : usersB) {
            assertEquals(entBId, u.getEntreprise().getId());
            assertNull(u.getMotDePasse());
        }

        MDC.put("idEntreprise", entCId.toString());
        List<UtilisateurDto> usersC = utilisateurService.findAll();
        assertFalse(usersC.isEmpty());
        for (UtilisateurDto u : usersC) {
            assertEquals(entCId, u.getEntreprise().getId());
            assertNull(u.getMotDePasse());
        }
    }

    @Test
    void testUtilisateurFindById_CrossEnterpriseThrowsNotFound() {
        MDC.put("idEntreprise", entAId.toString());
        List<UtilisateurDto> usersA = utilisateurService.findAll();
        assertFalse(usersA.isEmpty());
        Integer userIdFromA = usersA.get(0).getId();

        flushAndClear();
        MDC.put("idEntreprise", entBId.toString());
        Integer crossUserId = userIdFromA;
        assertThrows(EntityNotFoundException.class, () ->
                utilisateurService.findById(crossUserId)
        );
    }

    @Test
    void testUtilisateurFindById_OwnEnterpriseReturnsUser() {
        MDC.put("idEntreprise", entBId.toString());
        List<UtilisateurDto> usersB = utilisateurService.findAll();
        assertFalse(usersB.isEmpty());

        UtilisateurDto found = utilisateurService.findById(usersB.get(0).getId());
        assertNotNull(found);
        assertEquals(entBId, found.getEntreprise().getId());
        assertNull(found.getMotDePasse());
    }

    // ───── CATEGORY + ARTICLE ─────

    @Test
    void testArticleFindAll_EachEnterpriseSeesOnlyOwnArticles() {
        Integer catAId = createCategoryFor(entAId, "CAT-ISO-A").getId();
        Integer catBId = createCategoryFor(entBId, "CAT-ISO-B").getId();

        createArticleFor(entAId, "ISO-ART-A", catAId);
        createArticleFor(entBId, "ISO-ART-B", catBId);

        MDC.put("idEntreprise", entAId.toString());
        List<ArticleDto> articlesA = articleService.findAll();
        assertEquals(1, articlesA.size());
        assertEquals("ISO-ART-A", articlesA.get(0).getCodeArticle());

        MDC.put("idEntreprise", entBId.toString());
        List<ArticleDto> articlesB = articleService.findAll();
        assertEquals(1, articlesB.size());
        assertEquals("ISO-ART-B", articlesB.get(0).getCodeArticle());
    }

    @Test
    void testArticleFindById_CrossEnterpriseThrowsNotFound() {
        Integer catAId = createCategoryFor(entAId, "CAT-ISO-CROSS").getId();
        ArticleDto articleA = createArticleFor(entAId, "ISO-ART-CROSS", catAId);

        MDC.put("idEntreprise", entBId.toString());
        assertThrows(EntityNotFoundException.class, () ->
                articleService.findById(articleA.getId())
        );
    }

    @Test
    void testArticleFindById_OwnEnterpriseReturnsArticle() {
        Integer catBId = createCategoryFor(entBId, "CAT-ISO-OWN").getId();
        ArticleDto articleB = createArticleFor(entBId, "ISO-ART-OWN", catBId);

        MDC.put("idEntreprise", entBId.toString());
        ArticleDto found = articleService.findById(articleB.getId());
        assertNotNull(found);
        assertEquals("ISO-ART-OWN", found.getCodeArticle());
    }

    @Test
    void testCategoryFindAll_EachEnterpriseSeesOnlyOwnCategories() {
        createCategoryFor(entAId, "CAT-ISO-FA");
        createCategoryFor(entBId, "CAT-ISO-FB");

        MDC.put("idEntreprise", entAId.toString());
        List<CategoryDto> catsA = categoryService.findAll();
        assertEquals(1, catsA.size());
        assertTrue(catsA.get(0).getCode().contains("A"));

        MDC.put("idEntreprise", entBId.toString());
        List<CategoryDto> catsB = categoryService.findAll();
        assertEquals(1, catsB.size());
        assertTrue(catsB.get(0).getCode().contains("B"));
    }

    @Test
    void testCategorySave_WithWrongEnterpriseCategoryNotFound() {
        MDC.put("idEntreprise", entAId.toString());
        Integer catAId = createCategoryFor(entAId, "CAT-ISO-WRONG").getId();

        MDC.put("idEntreprise", entBId.toString());
        assertThrows(EntityNotFoundException.class, () ->
                categoryService.findById(catAId)
        );
    }

    @Test
    void testArticleSave_WithCrossEnterpriseCategoryThrowsNotFound() {
        Integer catAId = createCategoryFor(entAId, "CAT-ART-CROSS").getId();

        MDC.put("idEntreprise", entBId.toString());
        assertThrows(EntityNotFoundException.class, () ->
                articleService.save(ArticleDto.builder()
                        .codeArticle("FAIL-ART")
                        .designation("Should fail")
                        .prixUnitaireht(BigDecimal.valueOf(50))
                        .tauxTva(BigDecimal.valueOf(20))
                        .prixTtc(BigDecimal.valueOf(60))
                        .stock(BigDecimal.valueOf(10))
                        .category(CategoryDto.builder().id(catAId).build())
                        .entreprise(EntrepriseDto.builder().id(entBId).build())
                        .build())
        );
    }

    // ───── FACTURE ─────

    @Test
    void testFactureFindAll_EachEnterpriseSeesOnlyOwnFactures() {
        ClientDto clientA = createClientFor(entAId, "client-a-fact@test.com");
        createClientFor(entBId, "client-b-fact@test.com");
        createFactureFor(entAId, "FAC-ISO-A", clientA.getId());

        MDC.put("idEntreprise", entAId.toString());
        assertEquals(1, factureService.findAll().size());

        MDC.put("idEntreprise", entBId.toString());
        assertEquals(0, factureService.findAll().size());
    }

    @Test
    void testFactureFindById_CrossEnterpriseThrowsNotFound() {
        ClientDto clientA = createClientFor(entAId, "client-a-fact2@test.com");
        FactureDto factureA = createFactureFor(entAId, "FAC-ISO-CROSS", clientA.getId());

        MDC.put("idEntreprise", entBId.toString());
        assertThrows(EntityNotFoundException.class, () ->
                factureService.findById(factureA.getId())
        );
    }

    @Test
    void testClientFindAll_EachEnterpriseSeesOnlyOwnClients() {
        createClientFor(entAId, "cli-a@test.com");
        createClientFor(entBId, "cli-b@test.com");

        MDC.put("idEntreprise", entAId.toString());
        assertEquals(1, clientService.findAll().size());

        MDC.put("idEntreprise", entBId.toString());
        assertEquals(1, clientService.findAll().size());
    }

    @Test
    void testClientFindById_CrossEnterpriseThrowsNotFound() {
        ClientDto clientA = createClientFor(entAId, "cli-cross@test.com");

        MDC.put("idEntreprise", entBId.toString());
        assertThrows(EntityNotFoundException.class, () ->
                clientService.findById(clientA.getId())
        );
    }

    // ───── GLOBAL ISOLATION ─────

    @Test
    void testGlobalFindAll_NoDataLeakBetweenThreeEnterprises() {
        Integer catAId = createCategoryFor(entAId, "G-CAT-A").getId();
        Integer catBId = createCategoryFor(entBId, "G-CAT-B").getId();
        Integer catCId = createCategoryFor(entCId, "G-CAT-C").getId();

        createArticleFor(entAId, "G-ART-A", catAId);
        createArticleFor(entBId, "G-ART-B", catBId);
        createArticleFor(entCId, "G-ART-C", catCId);

        ClientDto clientA = createClientFor(entAId, "g-a@test.com");
        ClientDto clientB = createClientFor(entBId, "g-b@test.com");
        createFactureFor(entAId, "G-FAC-A", clientA.getId());
        createFactureFor(entBId, "G-FAC-B", clientB.getId());

        MDC.put("idEntreprise", entAId.toString());
        assertEquals(1, categoryService.findAll().size());
        assertEquals(1, articleService.findAll().size());
        assertEquals(1, clientService.findAll().size());
        assertEquals(1, factureService.findAll().size());

        MDC.put("idEntreprise", entBId.toString());
        assertEquals(1, categoryService.findAll().size());
        assertEquals(1, articleService.findAll().size());
        assertEquals(1, clientService.findAll().size());
        assertEquals(1, factureService.findAll().size());

        MDC.put("idEntreprise", entCId.toString());
        assertEquals(1, categoryService.findAll().size());
        assertEquals(1, articleService.findAll().size());
        assertEquals(0, clientService.findAll().size());
        assertEquals(0, factureService.findAll().size());
    }

    @Test
    void testFullScenario_EachEnterpriseOperatesIndependently() {
        Integer catAId = createCategoryFor(entAId, "SC-CAT-A").getId();
        Integer catBId = createCategoryFor(entBId, "SC-CAT-B").getId();
        Integer catCId = createCategoryFor(entCId, "SC-CAT-C").getId();

        ArticleDto artA = createArticleFor(entAId, "SC-ART-A", catAId);
        ArticleDto artB = createArticleFor(entBId, "SC-ART-B", catBId);
        ArticleDto artC = createArticleFor(entCId, "SC-ART-C", catCId);

        ClientDto cliA = createClientFor(entAId, "sc-a@test.com");
        ClientDto cliB = createClientFor(entBId, "sc-b@test.com");
        FactureDto facA = createFactureFor(entAId, "SC-FAC-A", cliA.getId());
        FactureDto facB = createFactureFor(entBId, "SC-FAC-B", cliB.getId());

        flushAndClear();
        MDC.put("idEntreprise", entAId.toString());
        assertEquals(1, articleService.findAll().size());
        assertEquals(1, clientService.findAll().size());
        assertEquals(1, factureService.findAll().size());
        assertNotNull(articleService.findById(artA.getId()));
        assertThrows(EntityNotFoundException.class, () -> articleService.findById(artB.getId()));
        assertThrows(EntityNotFoundException.class, () -> articleService.findById(artC.getId()));
        assertThrows(EntityNotFoundException.class, () -> factureService.findById(facB.getId()));

        flushAndClear();
        MDC.put("idEntreprise", entBId.toString());
        assertEquals(1, articleService.findAll().size());
        assertEquals(1, clientService.findAll().size());
        assertEquals(1, factureService.findAll().size());
        assertNotNull(articleService.findById(artB.getId()));
        assertThrows(EntityNotFoundException.class, () -> articleService.findById(artA.getId()));
        assertThrows(EntityNotFoundException.class, () -> articleService.findById(artC.getId()));
        assertThrows(EntityNotFoundException.class, () -> clientService.findById(cliA.getId()));
        assertThrows(EntityNotFoundException.class, () -> factureService.findById(facA.getId()));

        flushAndClear();
        MDC.put("idEntreprise", entCId.toString());
        assertEquals(1, articleService.findAll().size());
        assertEquals(0, clientService.findAll().size());
        assertNotNull(articleService.findById(artC.getId()));
        assertThrows(EntityNotFoundException.class, () -> articleService.findById(artA.getId()));
        assertThrows(EntityNotFoundException.class, () -> articleService.findById(artB.getId()));
    }
}
