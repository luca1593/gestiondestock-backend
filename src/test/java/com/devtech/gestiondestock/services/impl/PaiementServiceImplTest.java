package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.model.Paiement;
import com.devtech.gestiondestock.services.ClientService;
import com.devtech.gestiondestock.services.FactureService;
import com.devtech.gestiondestock.services.PaiementService;
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
class PaiementServiceImplTest {

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private FactureService factureService;

    @Autowired
    private ClientService clientService;

    private PaiementDto validPaiement;

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
                .nom("Test Client for Paiement")
                .prenom("Test")
                .email("client-paiement@test.com")
                .numTel("0123456789")
                .adresse(adresse)
                .build());

        FactureDto facture = factureService.save(FactureDto.builder()
                .numeroFacture("FAC-PAIE-TEST")
                .dateFacture(LocalDateTime.now())
                .statut(Facture.StatutFacture.EN_ATTENTE)
                .montantHT(BigDecimal.valueOf(1000))
                .montantTVA(BigDecimal.valueOf(200))
                .montantTTC(BigDecimal.valueOf(1200))
                .montantPaye(BigDecimal.ZERO)
                .montantRestant(BigDecimal.valueOf(1200))
                .clientId(client.getId())
                .entrepriseId(1)
                .build());

        validPaiement = PaiementDto.builder()
                .montant(BigDecimal.valueOf(500))
                .datePaiement(LocalDateTime.now())
                .modePaiement(Paiement.ModePaiement.CARTE_BANCAIRE)
                .reference("PAIE-REF-001")
                .factureId(facture.getId())
                .entrepriseId(1)
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldSavePaiementWithSuccess() {
        PaiementDto saved = paiementService.save(validPaiement);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(0, validPaiement.getMontant().compareTo(saved.getMontant()));
        assertEquals(validPaiement.getModePaiement(), saved.getModePaiement());
    }

    @Test
    void shouldThrowWhenSavingInvalidPaiement() {
        assertThrows(InvalidEntityException.class, () ->
                paiementService.save(PaiementDto.builder().build())
        );
    }

    @Test
    void shouldFindById() {
        PaiementDto saved = paiementService.save(validPaiement);
        PaiementDto found = paiementService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                paiementService.findById(-999)
        );
    }

    @Test
    void shouldFindAll() {
        paiementService.save(validPaiement);
        List<PaiementDto> list = paiementService.findAll();
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldDelete() {
        PaiementDto saved = paiementService.save(validPaiement);
        paiementService.delete(saved.getId());
        assertThrows(EntityNotFoundException.class, () ->
                paiementService.findById(saved.getId())
        );
    }
}
