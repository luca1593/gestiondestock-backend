package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.CommandeFournisseur;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.model.Fournisseur;
import com.devtech.gestiondestock.model.LigneCommandeFournisseur;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.CommandeFournisseurRepository;
import com.devtech.gestiondestock.repository.FournisseurRepository;
import com.devtech.gestiondestock.repository.LigneCommandeFournisseurRepository;
import com.devtech.gestiondestock.services.MvtStkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link CommandeFournisseurServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class CommandeFournisseurServiceImplTest {

    @Mock
    private CommandeFournisseurRepository commandeFournisseurRepository;
    @Mock
    private LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;
    @Mock
    private FournisseurRepository fournisseurRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private MvtStkService mvtStkService;

    @InjectMocks
    private CommandeFournisseurServiceImpl commandeFournisseurService;

    private Fournisseur fournisseur;
    private Article article;
    private CommandeFournisseur commandeFournisseur;

    @BeforeEach
    void setUp() {
        fournisseur = new Fournisseur();
        fournisseur.setId(1);
        fournisseur.setNom("Fournisseur SA");
        fournisseur.setPrenom("Paul");
        fournisseur.setEmail("fournisseur@test.com");

        Category category = new Category();
        category.setId(1);
        category.setCode("CAT-001");
        category.setDesignation("Catégorie test");

        article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Article test");
        article.setPrixUnitaireht(BigDecimal.valueOf(10));
        article.setTauxTva(BigDecimal.valueOf(20));
        article.setPrixTtc(BigDecimal.valueOf(12));
        article.setCategory(category);
        article.setStock(BigDecimal.valueOf(100));

        commandeFournisseur = new CommandeFournisseur();
        commandeFournisseur.setId(1);
        commandeFournisseur.setCode("CF-001");
        commandeFournisseur.setDateCommande(Instant.now());
        commandeFournisseur.setEtatcommande(EtatCommande.EN_PREPARATION);
        commandeFournisseur.setFournisseur(fournisseur);
        commandeFournisseur.setIdentreprise(1);

        lenient().when(commandeFournisseurRepository.save(any(CommandeFournisseur.class))).thenAnswer(inv -> {
            CommandeFournisseur saved = inv.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(10);
            }
            return saved;
        });
        lenient().when(ligneCommandeFournisseurRepository.save(any(LigneCommandeFournisseur.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    private AdresseDto adresseValide() {
        return AdresseDto.builder()
                .adresse1("1 rue test")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private CommandeFournisseurDto dtoValide() {
        return CommandeFournisseurDto.builder()
                .code("CF-001")
                .dateCommande(Instant.now())
                .etatcommande(EtatCommande.EN_PREPARATION)
                .fournisseur(FournisseurDto.builder().id(1).build())
                .identreprise(1)
                .ligneCommandeFournisseurs(Arrays.asList(LigneCommandeFournisseurDto.builder()
                        .article(ArticleDto.builder().id(1).build())
                        .quantite(BigDecimal.valueOf(2))
                        .prixUnitaire(BigDecimal.valueOf(10))
                        .build()))
                .build();
    }

    private LigneCommandeFournisseur ligneCommandeFournisseur() {
        LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
        ligne.setId(1);
        ligne.setArticle(article);
        ligne.setQuantite(BigDecimal.valueOf(2));
        ligne.setPrixUnitaire(BigDecimal.valueOf(10));
        ligne.setIdentreprise(1);
        return ligne;
    }

    // ---------------------------------------------------------------------
    // save
    // ---------------------------------------------------------------------

    @Test
    void save_quandDtoInvalide_leveInvalidEntityException_etRepositoryJamaisAppele() {
        assertThrows(InvalidEntityException.class,
                () -> commandeFournisseurService.save(CommandeFournisseurDto.builder().build()));

        verify(commandeFournisseurRepository, never()).save(any());
    }

    @Test
    void save_quandFournisseurInexistant_leveEntityNotFoundException() {
        CommandeFournisseurDto dto = dtoValide();
        dto.setFournisseur(FournisseurDto.builder().id(99).build());
        when(fournisseurRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandeFournisseurService.save(dto));

        verify(commandeFournisseurRepository, never()).save(any());
    }

    @Test
    void save_quandArticleNullDansLigne_leveInvalidEntityException() {
        CommandeFournisseurDto dto = dtoValide();
        dto.getLigneCommandeFournisseurs().get(0).setArticle(null);
        when(fournisseurRepository.findById(1)).thenReturn(Optional.of(fournisseur));

        assertThrows(InvalidEntityException.class, () -> commandeFournisseurService.save(dto));

        verify(commandeFournisseurRepository, never()).save(any());
    }

    @Test
    void save_quandArticleInexistant_leveInvalidEntityException() {
        CommandeFournisseurDto dto = dtoValide();
        dto.getLigneCommandeFournisseurs().get(0).setArticle(ArticleDto.builder().id(99).build());
        when(fournisseurRepository.findById(1)).thenReturn(Optional.of(fournisseur));
        when(articleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(InvalidEntityException.class, () -> commandeFournisseurService.save(dto));

        verify(commandeFournisseurRepository, never()).save(any());
    }

    @Test
    void save_quandValide_sauvegardeCommandeEtLignes_etEnregistreMouvementStock() {
        when(fournisseurRepository.findById(1)).thenReturn(Optional.of(fournisseur));
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(10))
                .thenReturn(Arrays.asList(ligneCommandeFournisseur()));

        CommandeFournisseurDto result = commandeFournisseurService.save(dtoValide());

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("CF-001", result.getCode());
        verify(ligneCommandeFournisseurRepository, times(1)).save(any(LigneCommandeFournisseur.class));
        verify(mvtStkService, times(1)).entreMvtStk(any(MvtStkDto.class));
    }

    // ---------------------------------------------------------------------
    // findById / findByCodeCommande / findByDateCommande
    // ---------------------------------------------------------------------

    @Test
    void findById_quandIdNull_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class, () -> commandeFournisseurService.findById(null));
        verify(commandeFournisseurRepository, never()).findById(any());
    }

    @Test
    void findById_quandInexistant_leveEntityNotFoundException() {
        when(commandeFournisseurRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandeFournisseurService.findById(999));
    }

    @Test
    void findById_quandExistant_retourneDto() {
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));

        CommandeFournisseurDto result = commandeFournisseurService.findById(1);

        assertNotNull(result);
        assertEquals("CF-001", result.getCode());
    }

    @Test
    void findByCodeCommande_quandCodeVide_retourneNull() {
        assertNull(commandeFournisseurService.findByCodeCommande(""));
        verify(commandeFournisseurRepository, never()).findCommandeFournisseurByCode(any());
    }

    @Test
    void findByCodeCommande_quandInexistant_leveEntityNotFoundException() {
        when(commandeFournisseurRepository.findCommandeFournisseurByCode("INCONNU"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commandeFournisseurService.findByCodeCommande("INCONNU"));
    }

    @Test
    void findByDateCommande_quandDateNull_retourneListeVide() {
        List<CommandeFournisseurDto> result = commandeFournisseurService.findByDateCommande(null);

        assertTrue(result.isEmpty());
        verify(commandeFournisseurRepository, never()).findCommandeFournisseurByDateCommande(any());
    }

    @Test
    void findAll_retourneListe() {
        when(commandeFournisseurRepository.findAll()).thenReturn(Arrays.asList(commandeFournisseur));

        List<CommandeFournisseurDto> result = commandeFournisseurService.findAll();

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------------------
    // updateEtatCommande
    // ---------------------------------------------------------------------

    @Test
    void updateEtatCommande_quandCommandeLivree_leveInvalidOpperatioException() {
        CommandeFournisseur livree = new CommandeFournisseur();
        livree.setId(1);
        livree.setEtatcommande(EtatCommande.LIVREE);
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(livree));

        assertThrows(InvalidOpperatioException.class,
                () -> commandeFournisseurService.updateEtatCommande(1, EtatCommande.VALIDEE));

        verify(commandeFournisseurRepository, never()).save(any());
    }

    @Test
    void updateEtatCommande_quandValide_enregistreLeNouvelEtat() {
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));

        CommandeFournisseurDto result =
                commandeFournisseurService.updateEtatCommande(1, EtatCommande.VALIDEE);

        assertNotNull(result);
        assertEquals(EtatCommande.VALIDEE, result.getEtatcommande());
    }

    @Test
    void updateEtatCommande_versLivree_enregistreMouvementStock() {
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));
        when(ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(1))
                .thenReturn(Arrays.asList(ligneCommandeFournisseur()));

        commandeFournisseurService.updateEtatCommande(1, EtatCommande.LIVREE);

        verify(mvtStkService, times(1)).entreMvtStk(any(MvtStkDto.class));
    }

    // ---------------------------------------------------------------------
    // updateQuantiterCommande
    // ---------------------------------------------------------------------

    @Test
    void updateQuantiterCommande_quandQuantiteZero_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class,
                () -> commandeFournisseurService.updateQuantiterCommande(1, 1, BigDecimal.ZERO));

        verify(commandeFournisseurRepository, never()).findById(any());
    }

    @Test
    void updateQuantiterCommande_quandValide_metAJourLaLigne() {
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));
        when(ligneCommandeFournisseurRepository.findById(1))
                .thenReturn(Optional.of(ligneCommandeFournisseur()));

        CommandeFournisseurDto result =
                commandeFournisseurService.updateQuantiterCommande(1, 1, BigDecimal.valueOf(7));

        assertNotNull(result);
        verify(ligneCommandeFournisseurRepository).save(any(LigneCommandeFournisseur.class));
    }

    // ---------------------------------------------------------------------
    // updateFournisseur / updateArticle / deleteArticle
    // ---------------------------------------------------------------------

    @Test
    void updateFournisseur_quandFournisseurInexistant_leveEntityNotFoundException() {
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));
        when(fournisseurRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commandeFournisseurService.updateFournisseur(1, 99));
    }

    @Test
    void updateFournisseur_quandValide_metAJourLeFournisseur() {
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));
        when(fournisseurRepository.findById(2)).thenReturn(Optional.of(fournisseur));

        CommandeFournisseurDto result = commandeFournisseurService.updateFournisseur(1, 2);

        assertNotNull(result);
        verify(commandeFournisseurRepository).save(any(CommandeFournisseur.class));
    }

    @Test
    void updateArticle_quandArticleInexistant_leveEntityNotFoundException() {
        when(articleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commandeFournisseurService.updateArticle(1, 1, 99));

        verify(commandeFournisseurRepository, never()).findById(any());
    }

    @Test
    void updateArticle_quandValide_metAJourLaLigne() {
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));
        when(ligneCommandeFournisseurRepository.findById(1))
                .thenReturn(Optional.of(ligneCommandeFournisseur()));

        CommandeFournisseurDto result = commandeFournisseurService.updateArticle(1, 1, 1);

        assertNotNull(result);
        verify(ligneCommandeFournisseurRepository).save(any(LigneCommandeFournisseur.class));
    }

    @Test
    void deleteArticle_quandValide_supprimeLaLigne() {
        when(commandeFournisseurRepository.findById(1)).thenReturn(Optional.of(commandeFournisseur));
        when(ligneCommandeFournisseurRepository.findById(1))
                .thenReturn(Optional.of(ligneCommandeFournisseur()));

        commandeFournisseurService.deleteArticle(1, 1);

        verify(ligneCommandeFournisseurRepository, times(1)).deleteById(1);
    }

    // ---------------------------------------------------------------------
    // findAllByCommandeFournisseur / findAllByFournisseurDto
    // ---------------------------------------------------------------------

    @Test
    void findAllByCommandeFournisseur_quandIdNull_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class,
                () -> commandeFournisseurService.findAllByCommandeFournisseur(null));

        verify(ligneCommandeFournisseurRepository, never()).findAllByCommandeFournisseurId(any());
    }

    @Test
    void findAllByCommandeFournisseur_quandValide_retourneLignes() {
        when(ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(1))
                .thenReturn(Arrays.asList(ligneCommandeFournisseur()));

        List<LigneCommandeFournisseurDto> result =
                commandeFournisseurService.findAllByCommandeFournisseur(1);

        assertEquals(1, result.size());
    }

    @Test
    void findAllByFournisseurDto_quandFournisseurInvalide_leveInvalidEntityException() {
        assertThrows(InvalidEntityException.class,
                () -> commandeFournisseurService.findAllByFournisseurDto(FournisseurDto.builder().build()));

        verify(commandeFournisseurRepository, never()).findAllByFournisseur(any());
    }

    @Test
    void findAllByFournisseurDto_quandValide_retourneListe() {
        when(commandeFournisseurRepository.findAllByFournisseur(any(Fournisseur.class)))
                .thenReturn(Arrays.asList(commandeFournisseur));

        FournisseurDto fournisseurDto = FournisseurDto.builder()
                .nom("Fournisseur SA").prenom("Paul").email("fournisseur@test.com").numTel("0102030405")
                .adresse(adresseValide())
                .build();

        List<CommandeFournisseurDto> result =
                commandeFournisseurService.findAllByFournisseurDto(fournisseurDto);

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------------------
    // delete
    // ---------------------------------------------------------------------

    @Test
    void delete_quandIdNull_neFaitRien() {
        commandeFournisseurService.delete(null);

        verify(commandeFournisseurRepository, never()).deleteById(any());
    }

    @Test
    void delete_appelleRepository() {
        commandeFournisseurService.delete(1);

        verify(commandeFournisseurRepository, times(1)).deleteById(1);
    }
}
