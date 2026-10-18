package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.MvtStk;
import com.devtech.gestiondestock.model.TypeMvt;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.MvtStkRepository;
import com.devtech.gestiondestock.services.AlertStockService;
import com.devtech.gestiondestock.services.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link MvtStkServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class MvtStkServiceImplTest {

    @Mock
    private MvtStkRepository mvtStkRepository;
    @Mock
    private ArticleService articleService;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private AlertStockService alertStockService;

    @InjectMocks
    private MvtStkServiceImpl mvtStkService;

    private Article article;
    private MvtStk mvtStk;

    @BeforeEach
    void setUp() {
        article = new Article();
        article.setId(1);
        article.setDesignation("Article test");
        article.setStock(BigDecimal.valueOf(100));

        mvtStk = new MvtStk();
        mvtStk.setId(1);
        mvtStk.setDateMvt(Instant.now());
        mvtStk.setQuantite(BigDecimal.valueOf(5));
        mvtStk.setTypeMvt(TypeMvt.ENTRER);
        mvtStk.setArticle(article);
        mvtStk.setIdentreprise(1);
    }

    private MvtStkDto dtoValide() {
        return MvtStkDto.builder()
                .dateMvt(Instant.now())
                .quantite(BigDecimal.valueOf(5))
                .article(ArticleDto.builder().id(1).build())
                .typeMvt(TypeMvt.ENTRER)
                .identreprise(1)
                .build();
    }

    // ---------------------------------------------------------------------
    // findById
    // ---------------------------------------------------------------------

    @Test
    void findById_quandIdNull_retourneNull() {
        MvtStkDto result = mvtStkService.findById(null);

        assertNull(result);
        verify(mvtStkRepository, never()).findById(any());
    }

    @Test
    void findById_quandIdExistant_retourneDto() {
        when(mvtStkRepository.findById(1)).thenReturn(Optional.of(mvtStk));

        MvtStkDto result = mvtStkService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(TypeMvt.ENTRER, result.getTypeMvt());
    }

    /**
     * Verrouille le correctif : un id inexistant doit lever {@link EntityNotFoundException}
     * et non {@link java.util.NoSuchElementException}.
     */
    @Test
    void findById_quandIdInexistant_leveEntityNotFoundException() {
        when(mvtStkRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(EntityNotFoundException.class,
                () -> mvtStkService.findById(999));

        assertNotNull(exception);
        assertFalse(exception instanceof java.util.NoSuchElementException);
    }

    // ---------------------------------------------------------------------
    // findMvtStkByType
    // ---------------------------------------------------------------------

    @Test
    void findMvtStkByType_quandTypeVide_retourneNull() {
        assertNull(mvtStkService.findMvtStkByType(""));
        assertNull(mvtStkService.findMvtStkByType(null));
        verify(mvtStkRepository, never()).findMvtStkByTypeMvt(any());
    }

    @Test
    void findMvtStkByType_quandTypeInconnu_retourneNull() {
        assertNull(mvtStkService.findMvtStkByType("PAS_UN_TYPE"));
        verify(mvtStkRepository, never()).findMvtStkByTypeMvt(any());
    }

    @Test
    void findMvtStkByType_quandTypeValide_retourneListe() {
        when(mvtStkRepository.findMvtStkByTypeMvt(TypeMvt.ENTRER)).thenReturn(Arrays.asList(mvtStk));

        var result = mvtStkService.findMvtStkByType("ENTRER");

        assertEquals(1, result.size());
        assertEquals(TypeMvt.ENTRER, result.get(0).getTypeMvt());
    }

    // ---------------------------------------------------------------------
    // save
    // ---------------------------------------------------------------------

    @Test
    void save_quandDtoInvalide_leveInvalidEntityException_etRepositoryJamaisAppele() {
        MvtStkDto dto = MvtStkDto.builder().build();

        assertThrows(InvalidEntityException.class, () -> mvtStkService.save(dto));

        verify(mvtStkRepository, never()).save(any());
    }

    @Test
    void save_quandDtoValide_retourneDto() {
        when(mvtStkRepository.save(any(MvtStk.class))).thenAnswer(inv -> inv.getArgument(0));

        MvtStkDto result = mvtStkService.save(dtoValide());

        assertNotNull(result);
        assertEquals(TypeMvt.ENTRER, result.getTypeMvt());
        verify(mvtStkRepository, times(1)).save(any(MvtStk.class));
    }

    // ---------------------------------------------------------------------
    // entreMvtStk / sortieMvtStk
    // ---------------------------------------------------------------------

    @Test
    void entreMvtStk_forceTypeEntrer_etMetAJourStockEtAlertes() {
        MvtStkDto dto = dtoValide();
        dto.setTypeMvt(null); // le service doit forcer ENTRER

        when(mvtStkRepository.save(any(MvtStk.class))).thenAnswer(inv -> inv.getArgument(0));
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        mvtStkService.entreMvtStk(dto);

        ArgumentCaptor<MvtStk> captor = ArgumentCaptor.forClass(MvtStk.class);
        verify(mvtStkRepository).save(captor.capture());
        assertEquals(TypeMvt.ENTRER, captor.getValue().getTypeMvt());
        assertEquals(0, BigDecimal.valueOf(5).compareTo(captor.getValue().getQuantite()));

        // Le stock de l'article est incrémenté et les alertes recalculées.
        assertEquals(0, BigDecimal.valueOf(105).compareTo(article.getStock()));
        verify(articleRepository).save(article);
        verify(alertStockService).checkAndCreateAlerts(1);
    }

    @Test
    void sortieMvtStk_forceQuantiteNegative() {
        MvtStkDto dto = dtoValide();
        dto.setTypeMvt(null);

        when(mvtStkRepository.save(any(MvtStk.class))).thenAnswer(inv -> inv.getArgument(0));
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        mvtStkService.sortieMvtStk(dto);

        ArgumentCaptor<MvtStk> captor = ArgumentCaptor.forClass(MvtStk.class);
        verify(mvtStkRepository).save(captor.capture());
        assertEquals(TypeMvt.SORTIE, captor.getValue().getTypeMvt());
        assertEquals(0, BigDecimal.valueOf(-5).compareTo(captor.getValue().getQuantite()));
        assertEquals(0, BigDecimal.valueOf(95).compareTo(article.getStock()));
    }

    // ---------------------------------------------------------------------
    // stockReelArticle
    // ---------------------------------------------------------------------

    @Test
    void stockReelArticle_quandIdNull_retourneMoinsUn() {
        BigDecimal result = mvtStkService.stockReelArticle(null);

        assertEquals(0, BigDecimal.valueOf(-1).compareTo(result));
        verify(mvtStkRepository, never()).stockReelArticle(any());
    }

    @Test
    void stockReelArticle_quandIdValide_retourneStock() {
        when(mvtStkRepository.stockReelArticle(1)).thenReturn(BigDecimal.valueOf(42));

        BigDecimal result = mvtStkService.stockReelArticle(1);

        assertEquals(0, BigDecimal.valueOf(42).compareTo(result));
        verify(articleService).findById(1);
    }

    // ---------------------------------------------------------------------
    // delete
    // ---------------------------------------------------------------------

    @Test
    void delete_appelleRepository() {
        mvtStkService.delete(1);

        verify(mvtStkRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_quandIdNull_neFaitRien() {
        mvtStkService.delete(null);

        verify(mvtStkRepository, never()).deleteById(any());
    }
}
