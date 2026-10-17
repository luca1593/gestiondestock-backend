package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.dto.VenteDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.LigneVente;
import com.devtech.gestiondestock.model.Vente;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.LigneVenteRepository;
import com.devtech.gestiondestock.repository.VenteRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link VenteServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class VenteServiceImplTest {

    @Mock
    private VenteRepository venteRepository;
    @Mock
    private LigneVenteRepository ligneVenteRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private MvtStkService mvtStkService;

    @InjectMocks
    private VenteServiceImpl venteService;

    private Vente vente;
    private Article article;

    @BeforeEach
    void setUp() {
        vente = new Vente();
        vente.setId(1);
        vente.setCode("V-TEST001");
        vente.setDateVente(Instant.now());
        vente.setIdentreprise(1);

        article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Article test");
        article.setStock(BigDecimal.valueOf(100));
    }

    private VenteDto dtoValide() {
        return VenteDto.builder()
                .code("V-TEST001")
                .identreprise(1)
                .ligneVentes(Arrays.asList(LigneVenteDto.builder()
                        .article(ArticleDto.builder().id(1).build())
                        .quantite(BigDecimal.valueOf(2))
                        .prixUnitaire(BigDecimal.valueOf(10))
                        .build()))
                .build();
    }

    // ---------------------------------------------------------------------
    // save
    // ---------------------------------------------------------------------

    @Test
    void save_quandDtoInvalide_leveInvalidEntityException_etRepositoryJamaisAppele() {
        VenteDto dto = VenteDto.builder().build();

        assertThrows(InvalidEntityException.class, () -> venteService.save(dto));

        verify(venteRepository, never()).save(any());
    }

    @Test
    void save_quandArticleInexistant_leveInvalidEntityException() {
        VenteDto dto = VenteDto.builder()
                .code("V-TEST001")
                .identreprise(1)
                .ligneVentes(Arrays.asList(LigneVenteDto.builder()
                        .article(ArticleDto.builder().id(99).build())
                        .quantite(BigDecimal.valueOf(2))
                        .build()))
                .build();
        when(articleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(InvalidEntityException.class, () -> venteService.save(dto));

        verify(venteRepository, never()).save(any());
    }

    @Test
    void save_quandValide_sauvegardeVenteEtLignes_etEnregistreMouvementStock() {
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(venteRepository.save(any(Vente.class))).thenAnswer(inv -> {
            Vente saved = inv.getArgument(0);
            saved.setId(5);
            return saved;
        });
        when(ligneVenteRepository.save(any(LigneVente.class))).thenAnswer(inv -> inv.getArgument(0));

        VenteDto result = venteService.save(dtoValide());

        assertNotNull(result);
        assertEquals(5, result.getId());
        assertEquals("V-TEST001", result.getCode());
        verify(ligneVenteRepository, times(1)).save(any(LigneVente.class));
        verify(mvtStkService, times(1)).sortieMvtStk(any(MvtStkDto.class));
    }

    // ---------------------------------------------------------------------
    // findById
    // ---------------------------------------------------------------------

    @Test
    void findById_quandIdNull_leveEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> venteService.findById(null));
    }

    @Test
    void findById_quandInexistant_leveEntityNotFoundException() {
        when(venteRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> venteService.findById(999));
    }

    @Test
    void findById_quandExistant_retourneDto() {
        when(venteRepository.findById(1)).thenReturn(Optional.of(vente));

        VenteDto result = venteService.findById(1);

        assertNotNull(result);
        assertEquals("V-TEST001", result.getCode());
    }

    // ---------------------------------------------------------------------
    // findByCodeVente / findByDateVente
    // ---------------------------------------------------------------------

    @Test
    void findByCodeVente_quandCodeVide_leveEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> venteService.findByCodeVente(""));
        verify(venteRepository, never()).findVenteByCode(any());
    }

    @Test
    void findByCodeVente_quandInexistant_leveEntityNotFoundException() {
        when(venteRepository.findVenteByCode("INCONNU")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> venteService.findByCodeVente("INCONNU"));
    }

    @Test
    void findByDateVente_quandDateNull_retourneListeVide() {
        List<VenteDto> result = venteService.findByDateVente(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(venteRepository, never()).findVenteByDateVente(any());
    }

    // ---------------------------------------------------------------------
    // findAll / findAllLigneVenteByVente
    // ---------------------------------------------------------------------

    @Test
    void findAll_retourneListe() {
        when(venteRepository.findAll()).thenReturn(Arrays.asList(vente));

        List<VenteDto> result = venteService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findAllLigneVenteByVente_retourneLignes() {
        LigneVente ligne = new LigneVente();
        ligne.setId(1);
        ligne.setArticle(article);
        ligne.setQuantite(BigDecimal.valueOf(2));
        when(ligneVenteRepository.findAllByVenteId(1)).thenReturn(Arrays.asList(ligne));

        List<LigneVenteDto> result = venteService.findAllLigneVenteByVente(1);

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------------------
    // delete
    // ---------------------------------------------------------------------

    @Test
    void delete_quandVenteSansLigne_supprime() {
        when(ligneVenteRepository.findAllByVenteId(1)).thenReturn(Collections.emptyList());

        venteService.delete(1);

        verify(venteRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_quandVenteAvecLignes_leveInvalidOpperatioException() {
        LigneVente ligne = new LigneVente();
        ligne.setId(1);
        when(ligneVenteRepository.findAllByVenteId(1)).thenReturn(Arrays.asList(ligne));

        assertThrows(InvalidOpperatioException.class, () -> venteService.delete(1));

        verify(venteRepository, never()).deleteById(any());
    }
}
