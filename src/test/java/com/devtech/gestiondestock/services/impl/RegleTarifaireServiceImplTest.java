package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.RegleTarifaireDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.RegleTarifaire;
import com.devtech.gestiondestock.services.RegleTarifaireService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegleTarifaireServiceImplTest {

    @Autowired
    private RegleTarifaireService regleTarifaireService;

    private RegleTarifaireDto validRegle;

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");

        validRegle = RegleTarifaireDto.builder()
                .code("REGLE-TEST-001")
                .nom("Test Rule")
                .description("A test pricing rule")
                .typeRegle(RegleTarifaire.TypeRegle.REMISE_POURCENTAGE)
                .valeur(BigDecimal.valueOf(10))
                .estActif(true)
                .dateDebut(LocalDateTime.now())
                .entrepriseId(1)
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldSaveRegleWithSuccess() {
        RegleTarifaireDto saved = regleTarifaireService.save(validRegle);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(validRegle.getCode(), saved.getCode());
        assertEquals(validRegle.getNom(), saved.getNom());
    }

    @Test
    void shouldThrowWhenSavingInvalidRegle() {
        assertThrows(InvalidEntityException.class, () ->
                regleTarifaireService.save(RegleTarifaireDto.builder().build())
        );
    }

    @Test
    void shouldFindById() {
        RegleTarifaireDto saved = regleTarifaireService.save(validRegle);
        RegleTarifaireDto found = regleTarifaireService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                regleTarifaireService.findById(-999)
        );
    }

    @Test
    void shouldFindByCode() {
        RegleTarifaireDto saved = regleTarifaireService.save(validRegle);
        RegleTarifaireDto found = regleTarifaireService.findByCode(saved.getCode());

        assertNotNull(found);
        assertEquals(saved.getCode(), found.getCode());
    }

    @Test
    void shouldFindAll() {
        regleTarifaireService.save(validRegle);
        List<RegleTarifaireDto> list = regleTarifaireService.findAll();
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldFindActifs() {
        regleTarifaireService.save(validRegle);
        List<RegleTarifaireDto> actifs = regleTarifaireService.findActifs();
        assertFalse(actifs.isEmpty());
    }

    @Test
    void shouldDelete() {
        RegleTarifaireDto saved = regleTarifaireService.save(validRegle);
        regleTarifaireService.delete(saved.getId());
        assertThrows(EntityNotFoundException.class, () ->
                regleTarifaireService.findById(saved.getId())
        );
    }
}
