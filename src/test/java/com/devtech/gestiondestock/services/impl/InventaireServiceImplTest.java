package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.EntrepotDto;
import com.devtech.gestiondestock.dto.InventaireDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Inventaire;
import com.devtech.gestiondestock.services.EntrepotService;
import com.devtech.gestiondestock.services.InventaireService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InventaireServiceImplTest {

    @Autowired
    private InventaireService inventaireService;

    @Autowired
    private EntrepotService entrepotService;

    private InventaireDto validInventaire;

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");

        EntrepotDto entrepot = new EntrepotDto();
        entrepot.setCode("E-INV-TEST");
        entrepot.setNom("Entrepot for Inventaire test");
        entrepot.setEntrepriseId(1);
        entrepot = entrepotService.save(entrepot);

        validInventaire = InventaireDto.builder()
                .code("INV-TEST-001")
                .description("Test inventory")
                .dateDebut(LocalDateTime.now())
                .statut(Inventaire.Statut.EN_COURS)
                .entrepriseId(1)
                .entrepotId(entrepot.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldSaveInventaireWithSuccess() {
        InventaireDto saved = inventaireService.save(validInventaire);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(validInventaire.getCode(), saved.getCode());
        assertEquals(validInventaire.getStatut(), saved.getStatut());
    }

    @Test
    void shouldThrowWhenSavingInvalidInventaire() {
        assertThrows(InvalidEntityException.class, () ->
                inventaireService.save(InventaireDto.builder().build())
        );
    }

    @Test
    void shouldFindById() {
        InventaireDto saved = inventaireService.save(validInventaire);
        InventaireDto found = inventaireService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                inventaireService.findById(-999)
        );
    }

    @Test
    void shouldFindByCode() {
        InventaireDto saved = inventaireService.save(validInventaire);
        InventaireDto found = inventaireService.findByCode(saved.getCode());

        assertNotNull(found);
        assertEquals(saved.getCode(), found.getCode());
    }

    @Test
    void shouldFindAll() {
        inventaireService.save(validInventaire);
        List<InventaireDto> list = inventaireService.findAll();
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldDelete() {
        InventaireDto saved = inventaireService.save(validInventaire);
        inventaireService.delete(saved.getId());
        assertThrows(EntityNotFoundException.class, () ->
                inventaireService.findById(saved.getId())
        );
    }
}
