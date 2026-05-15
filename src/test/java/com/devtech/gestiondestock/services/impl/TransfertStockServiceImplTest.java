package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.EntrepotDto;
import com.devtech.gestiondestock.dto.TransfertStockDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.TransfertStock;
import com.devtech.gestiondestock.services.EntrepotService;
import com.devtech.gestiondestock.services.TransfertStockService;
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
class TransfertStockServiceImplTest {

    @Autowired
    private TransfertStockService transfertStockService;

    @Autowired
    private EntrepotService entrepotService;

    private TransfertStockDto validTransfert;

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");

        EntrepotDto source = new EntrepotDto();
        source.setCode("E-TRF-SRC");
        source.setNom("Source Entrepot");
        source.setEntrepriseId(1);
        source = entrepotService.save(source);

        EntrepotDto dest = new EntrepotDto();
        dest.setCode("E-TRF-DST");
        dest.setNom("Destination Entrepot");
        dest.setEntrepriseId(1);
        dest = entrepotService.save(dest);

        validTransfert = TransfertStockDto.builder()
                .code("TRF-TEST-001")
                .dateTransfert(LocalDateTime.now())
                .statut(TransfertStock.StatutTransfert.EN_ATTENTE)
                .entrepriseId(1)
                .entrepotSourceId(source.getId())
                .entrepotDestinationId(dest.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldSaveTransfertWithSuccess() {
        TransfertStockDto saved = transfertStockService.save(validTransfert);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(validTransfert.getCode(), saved.getCode());
    }

    @Test
    void shouldThrowWhenSavingInvalidTransfert() {
        assertThrows(InvalidEntityException.class, () ->
                transfertStockService.save(TransfertStockDto.builder().build())
        );
    }

    @Test
    void shouldFindById() {
        TransfertStockDto saved = transfertStockService.save(validTransfert);
        TransfertStockDto found = transfertStockService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                transfertStockService.findById(-999)
        );
    }

    @Test
    void shouldFindByCode() {
        TransfertStockDto saved = transfertStockService.save(validTransfert);
        TransfertStockDto found = transfertStockService.findByCode(saved.getCode());

        assertNotNull(found);
        assertEquals(saved.getCode(), found.getCode());
    }

    @Test
    void shouldFindAll() {
        transfertStockService.save(validTransfert);
        List<TransfertStockDto> list = transfertStockService.findAll();
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldDelete() {
        TransfertStockDto saved = transfertStockService.save(validTransfert);
        transfertStockService.delete(saved.getId());
        assertThrows(EntityNotFoundException.class, () ->
                transfertStockService.findById(saved.getId())
        );
    }
}
