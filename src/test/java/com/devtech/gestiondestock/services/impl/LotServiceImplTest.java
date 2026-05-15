package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.CategoryDto;
import com.devtech.gestiondestock.dto.EntrepotDto;
import com.devtech.gestiondestock.dto.LotDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.services.ArticleService;
import com.devtech.gestiondestock.services.CategoryService;
import com.devtech.gestiondestock.services.EntrepotService;
import com.devtech.gestiondestock.services.LotService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LotServiceImplTest {

    @Autowired
    private LotService lotService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EntrepotService entrepotService;

    private LotDto validLot;

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");

        CategoryDto category = categoryService.save(CategoryDto.builder()
                .code("CAT-LOT-TEST")
                .designation("Category for Lot test")
                .identreprise(1)
                .build());

        ArticleDto article = articleService.save(ArticleDto.builder()
                .codeArticle("ART-LOT-TEST")
                .designation("Article for Lot test")
                .prixUnitaireht(BigDecimal.valueOf(100))
                .tauxTva(BigDecimal.valueOf(20))
                .prixTtc(BigDecimal.valueOf(120))
                .category(category)
                .build());

        EntrepotDto entrepot = new EntrepotDto();
        entrepot.setCode("E-LOT-TEST");
        entrepot.setNom("Entrepot for Lot test");
        entrepot.setEntrepriseId(1);
        entrepot = entrepotService.save(entrepot);

        validLot = LotDto.builder()
                .numeroLot("LOT-TEST-001")
                .quantite(BigDecimal.valueOf(100))
                .quantiteRestante(BigDecimal.valueOf(100))
                .prixAchat(BigDecimal.valueOf(50))
                .articleId(article.getId())
                .entrepotId(entrepot.getId())
                .entrepriseId(1)
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldSaveLotWithSuccess() {
        LotDto saved = lotService.save(validLot);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(validLot.getNumeroLot(), saved.getNumeroLot());
        assertEquals(0, validLot.getQuantite().compareTo(saved.getQuantite()));
    }

    @Test
    void shouldThrowWhenSavingInvalidLot() {
        assertThrows(InvalidEntityException.class, () ->
                lotService.save(LotDto.builder().build())
        );
    }

    @Test
    void shouldFindById() {
        LotDto saved = lotService.save(validLot);
        LotDto found = lotService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals(saved.getNumeroLot(), found.getNumeroLot());
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                lotService.findById(-999)
        );
    }

    @Test
    void shouldFindByNumeroLot() {
        LotDto saved = lotService.save(validLot);
        LotDto found = lotService.findByNumeroLot(saved.getNumeroLot());

        assertNotNull(found);
        assertEquals(saved.getNumeroLot(), found.getNumeroLot());
    }

    @Test
    void shouldFindAll() {
        lotService.save(validLot);
        List<LotDto> lots = lotService.findAll();
        assertFalse(lots.isEmpty());
    }

    @Test
    void shouldDelete() {
        LotDto saved = lotService.save(validLot);
        lotService.delete(saved.getId());
        assertThrows(EntityNotFoundException.class, () ->
                lotService.findById(saved.getId())
        );
    }
}
