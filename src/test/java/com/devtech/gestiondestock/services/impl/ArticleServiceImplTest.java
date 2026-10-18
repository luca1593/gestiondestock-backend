package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.CategoryDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.LigneCommandeClient;
import com.devtech.gestiondestock.model.LigneCommandeFournisseur;
import com.devtech.gestiondestock.model.LigneVente;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.CategoryRepository;
import com.devtech.gestiondestock.repository.EntrepriseRepository;
import com.devtech.gestiondestock.repository.LigneCommandeClientRepository;
import com.devtech.gestiondestock.repository.LigneCommandeFournisseurRepository;
import com.devtech.gestiondestock.repository.LigneVenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link ArticleServiceImpl}.
 * On verrouille le comportement actuel du service : validations, acces repository et
 * regles de suppression.
 */
@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private LigneVenteRepository venteRepository;
    @Mock
    private LigneCommandeClientRepository commandeClientRepository;
    @Mock
    private LigneCommandeFournisseurRepository commandeFournisseurRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EntrepriseRepository entrepriseRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Category category;
    private Article article;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(5);
        category.setCode("CAT-001");
        category.setDesignation("Categorie test");
        category.setIdentreprise(1);

        article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Article test");
        article.setPrixUnitaireht(BigDecimal.valueOf(10));
        article.setTauxTva(BigDecimal.valueOf(20));
        article.setPrixTtc(BigDecimal.valueOf(12));
        article.setStock(BigDecimal.valueOf(50));
        article.setCategory(category);
    }

    private ArticleDto articleDtoValide() {
        return ArticleDto.builder()
                .id(1)
                .codeArticle("ART-001")
                .designation("Article test")
                .prixUnitaireht(BigDecimal.valueOf(10))
                .tauxTva(BigDecimal.valueOf(20))
                .prixTtc(BigDecimal.valueOf(12))
                .stock(BigDecimal.valueOf(50))
                .category(CategoryDto.builder().id(5).code("CAT-001").designation("Categorie test").build())
                .build();
    }

    // ------------------------------------------------------------------
    // save(...)
    // ------------------------------------------------------------------

    @Test
    void save_whenDtoInvalide_shouldThrowInvalidEntityException() {
        // Aucun champ renseigne -> le validateur retourne des erreurs
        ArticleDto dto = ArticleDto.builder().build();

        assertThrows(InvalidEntityException.class, () -> articleService.save(dto));

        // Le repository ne doit JAMAIS etre appele pour un DTO invalide
        verifyNoInteractions(articleRepository, categoryRepository);
    }

    @Test
    void save_whenCategorieInexistante_shouldThrowEntityNotFoundException() {
        ArticleDto dto = articleDtoValide();
        when(categoryRepository.findById(5)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> articleService.save(dto));

        assertTrue(ex.getMessage().contains("categorie"));
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void save_whenDtoValide_shouldReturnSavedDto() {
        ArticleDto dto = articleDtoValide();
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        ArticleDto result = articleService.save(dto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("ART-001", result.getCodeArticle());
        assertEquals("Article test", result.getDesignation());
        assertNotNull(result.getCategory());
        assertEquals(5, result.getCategory().getId());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void save_whenCodeArticleDejaExisteDansLEntreprise_shouldThrowInvalidEntityException() {
        // Creation (id null) d'un article dont le code est deja pris dans l'entreprise
        ArticleDto dto = articleDtoValide();
        dto.setId(null);
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));

        Article existant = new Article();
        existant.setId(42);
        existant.setCodeArticle("ART-001");
        when(articleRepository.findAllByCodeArticle("ART-001")).thenReturn(List.of(existant));

        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> articleService.save(dto));

        assertEquals(ErrorsCode.ARTICLE_CODE_ALREADY_EXISTS, ex.getErrorsCode());
        assertTrue(ex.getMessage().contains("ART-001"));
        // L'article ne doit PAS etre persiste si le code est en doublon
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void save_whenMemeArticleModifieSansChangerLeCode_shouldNotThrow() {
        // Modification de l'article lui-meme : il ne doit pas etre considere comme son propre doublon
        ArticleDto dto = articleDtoValide(); // id = 1
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));

        Article luiMeme = new Article();
        luiMeme.setId(1);
        luiMeme.setCodeArticle("ART-001");
        when(articleRepository.findAllByCodeArticle("ART-001")).thenReturn(List.of(luiMeme));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        ArticleDto result = articleService.save(dto);

        assertNotNull(result);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void save_sansEntreprise_doitReprendreLentrepriseDuJeton() {
        // Sans entreprise dans le DTO, l'article doit etre rattache au tenant du jeton
        // (sinon identreprise = NULL : article visible de toutes les entreprises).
        MDC.put("idEntreprise", "1");
        try {
            ArticleDto dto = articleDtoValide();
            dto.setId(null);
            dto.setEntreprise(null);
            when(categoryRepository.findById(5)).thenReturn(Optional.of(category));
            when(articleRepository.findAllByCodeArticle("ART-001")).thenReturn(List.of());

            Entreprise entreprise = new Entreprise();
            entreprise.setId(1);
            when(entrepriseRepository.findById(1)).thenReturn(Optional.of(entreprise));
            when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

            ArticleDto result = articleService.save(dto);

            assertNotNull(result.getEntreprise());
            assertEquals(1, result.getEntreprise().getId());
        } finally {
            MDC.clear();
        }
    }

    // ------------------------------------------------------------------
    // findById(...)
    // ------------------------------------------------------------------
    @Test
    void findById_whenIdNull_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> articleService.findById(null));
        verifyNoInteractions(articleRepository);
    }

    @Test
    void findById_whenArticleInexistant_shouldThrowEntityNotFoundException() {
        when(articleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> articleService.findById(99));
    }

    @Test
    void findById_whenArticleExistant_shouldReturnDto() {
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));

        ArticleDto result = articleService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("ART-001", result.getCodeArticle());
        assertEquals(BigDecimal.valueOf(10), result.getPrixUnitaireht());
    }

    // ------------------------------------------------------------------
    // findByCodeArticle(...)
    // ------------------------------------------------------------------

    @Test
    void findByCodeArticle_whenCodeVide_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> articleService.findByCodeArticle(""));
        assertThrows(EntityNotFoundException.class, () -> articleService.findByCodeArticle(null));
        verifyNoInteractions(articleRepository);
    }

    @Test
    void findByCodeArticle_whenArticleInexistant_shouldThrowEntityNotFoundException() {
        when(articleRepository.findArticleByCodeArticle("ART-999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> articleService.findByCodeArticle("ART-999"));
    }

    @Test
    void findByCodeArticle_whenArticleExistant_shouldReturnDto() {
        when(articleRepository.findArticleByCodeArticle("ART-001")).thenReturn(Optional.of(article));

        ArticleDto result = articleService.findByCodeArticle("ART-001");

        assertNotNull(result);
        assertEquals("ART-001", result.getCodeArticle());
    }

    // ------------------------------------------------------------------
    // findAll()
    // ------------------------------------------------------------------

    @Test
    void findAll_whenListeVide_shouldReturnListeVide() {
        when(articleRepository.findAll()).thenReturn(Collections.emptyList());

        List<ArticleDto> result = articleService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_whenListeNonVide_shouldReturnDtos() {
        when(articleRepository.findAll()).thenReturn(Arrays.asList(article));

        List<ArticleDto> result = articleService.findAll();

        assertEquals(1, result.size());
        assertEquals("ART-001", result.get(0).getCodeArticle());
    }

    // ------------------------------------------------------------------
    // findAllByCategorie(...)
    // ------------------------------------------------------------------

    @Test
    void findAllByCategorie_whenIdNull_shouldThrowInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class, () -> articleService.findAllByCategorie(null));
        verifyNoInteractions(articleRepository);
    }

    @Test
    void findAllByCategorie_whenCategorieValide_shouldReturnDtos() {
        when(articleRepository.findAllByCategoryId(5)).thenReturn(Arrays.asList(article));

        List<ArticleDto> result = articleService.findAllByCategorie(5);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    // ------------------------------------------------------------------
    // Historiques
    // ------------------------------------------------------------------

    @Test
    void findHistoriqueVente_whenIdNull_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> articleService.findHistoriqueVente(null));
        verifyNoInteractions(venteRepository);
    }

    @Test
    void findHistoriqueVente_whenLignesExistent_shouldReturnDtos() {
        LigneVente ligne = new LigneVente();
        ligne.setId(3);
        ligne.setQuantite(BigDecimal.valueOf(2));
        ligne.setPrixUnitaire(BigDecimal.valueOf(10));
        when(venteRepository.findAllByArticleId(1)).thenReturn(Arrays.asList(ligne));

        List<LigneVenteDto> result = articleService.findHistoriqueVente(1);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getId());
    }

    @Test
    void findHistoriqueCommandeClient_whenIdNull_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> articleService.findHistoriqueCommandeClient(null));
        verifyNoInteractions(commandeClientRepository);
    }

    @Test
    void findHistoriqueCommandeClient_whenLignesExistent_shouldReturnDtos() {
        LigneCommandeClient ligne = new LigneCommandeClient();
        ligne.setId(7);
        when(commandeClientRepository.findAllByArticleId(1)).thenReturn(Arrays.asList(ligne));

        List<LigneCommandeClientDto> result = articleService.findHistoriqueCommandeClient(1);

        assertEquals(1, result.size());
        assertEquals(7, result.get(0).getId());
    }

    @Test
    void findHistoriqueCommandeFournisseur_whenIdNull_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> articleService.findHistoriqueCommandeFournisseur(null));
        verifyNoInteractions(commandeFournisseurRepository);
    }

    @Test
    void findHistoriqueCommandeFournisseur_whenLignesExistent_shouldReturnDtos() {
        LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
        ligne.setId(9);
        when(commandeFournisseurRepository.findAllByArticleId(1)).thenReturn(Arrays.asList(ligne));

        List<LigneCommandeFournisseurDto> result = articleService.findHistoriqueCommandeFournisseur(1);

        assertEquals(1, result.size());
        assertEquals(9, result.get(0).getId());
    }

    // ------------------------------------------------------------------
    // delete(...)
    // ------------------------------------------------------------------

    @Test
    void delete_whenIdNull_shouldThrowEntityNotFoundExceptionSansAppelRepository() {
        assertThrows(EntityNotFoundException.class, () -> articleService.delete(null));
        verifyNoInteractions(articleRepository, venteRepository, commandeClientRepository, commandeFournisseurRepository);
    }

    @Test
    void delete_whenArticleUtiliseDansCommandeClient_shouldThrowInvalidOpperatioException() {
        LigneCommandeClient ligne = new LigneCommandeClient();
        ligne.setId(1);
        when(commandeClientRepository.findAllByArticleId(1)).thenReturn(Arrays.asList(ligne));

        assertThrows(InvalidOpperatioException.class, () -> articleService.delete(1));

        verify(articleRepository, never()).deleteById(anyInt());
    }

    @Test
    void delete_whenArticleUtiliseDansCommandeFournisseur_shouldThrowInvalidOpperatioException() {
        LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
        ligne.setId(1);
        when(commandeClientRepository.findAllByArticleId(1)).thenReturn(Collections.emptyList());
        when(commandeFournisseurRepository.findAllByArticleId(1)).thenReturn(Arrays.asList(ligne));

        assertThrows(InvalidOpperatioException.class, () -> articleService.delete(1));

        verify(articleRepository, never()).deleteById(anyInt());
    }

    @Test
    void delete_whenArticleUtiliseDansVente_shouldThrowInvalidOpperatioException() {
        LigneVente ligne = new LigneVente();
        ligne.setId(1);
        when(commandeClientRepository.findAllByArticleId(1)).thenReturn(Collections.emptyList());
        when(commandeFournisseurRepository.findAllByArticleId(1)).thenReturn(Collections.emptyList());
        when(venteRepository.findAllByArticleId(1)).thenReturn(Arrays.asList(ligne));

        assertThrows(InvalidOpperatioException.class, () -> articleService.delete(1));

        verify(articleRepository, never()).deleteById(anyInt());
    }

    @Test
    void delete_whenArticleLibre_shouldDeleteById() {
        when(commandeClientRepository.findAllByArticleId(1)).thenReturn(Collections.emptyList());
        when(commandeFournisseurRepository.findAllByArticleId(1)).thenReturn(Collections.emptyList());
        when(venteRepository.findAllByArticleId(1)).thenReturn(Collections.emptyList());

        articleService.delete(1);

        verify(articleRepository, times(1)).deleteById(1);
    }
}
