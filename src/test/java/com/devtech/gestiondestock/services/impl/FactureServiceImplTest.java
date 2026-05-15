package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.services.ClientService;
import com.devtech.gestiondestock.services.FactureService;
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
class FactureServiceImplTest {

    @Autowired
    private FactureService factureService;

    @Autowired
    private ClientService clientService;

    private FactureDto validFacture;

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");

        AdresseDto adresse = AdresseDto.builder()
                .adresse1("1 rue test")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();

        ClientDto client = clientService.save(ClientDto.builder()
                .nom("Test Client for Facture")
                .prenom("Test")
                .email("client-facture@test.com")
                .numTel("0123456789")
                .adresse(adresse)
                .build());

        validFacture = FactureDto.builder()
                .numeroFacture("FAC-TEST-001")
                .dateFacture(LocalDateTime.now())
                .statut(Facture.StatutFacture.EN_ATTENTE)
                .montantHT(BigDecimal.valueOf(1000))
                .montantTVA(BigDecimal.valueOf(200))
                .montantTTC(BigDecimal.valueOf(1200))
                .montantPaye(BigDecimal.ZERO)
                .montantRestant(BigDecimal.valueOf(1200))
                .clientId(client.getId())
                .entrepriseId(1)
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldSaveFactureWithSuccess() {
        FactureDto saved = factureService.save(validFacture);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(validFacture.getNumeroFacture(), saved.getNumeroFacture());
    }

    @Test
    void shouldThrowWhenSavingInvalidFacture() {
        assertThrows(InvalidEntityException.class, () ->
                factureService.save(FactureDto.builder().build())
        );
    }

    @Test
    void shouldFindById() {
        FactureDto saved = factureService.save(validFacture);
        FactureDto found = factureService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                factureService.findById(-999)
        );
    }

    @Test
    void shouldFindByNumeroFacture() {
        FactureDto saved = factureService.save(validFacture);
        FactureDto found = factureService.findByNumeroFacture(saved.getNumeroFacture());

        assertNotNull(found);
        assertEquals(saved.getNumeroFacture(), found.getNumeroFacture());
    }

    @Test
    void shouldFindAll() {
        factureService.save(validFacture);
        List<FactureDto> list = factureService.findAll();
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldDelete() {
        FactureDto saved = factureService.save(validFacture);
        factureService.delete(saved.getId());
        assertThrows(EntityNotFoundException.class, () ->
                factureService.findById(saved.getId())
        );
    }
}
