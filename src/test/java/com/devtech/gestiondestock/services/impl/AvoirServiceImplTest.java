package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AvoirDto;
import com.devtech.gestiondestock.model.Avoir;
import com.devtech.gestiondestock.repository.AvoirRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvoirServiceImplTest {

    @Mock
    private AvoirRepository avoirRepository;

    @InjectMocks
    private AvoirServiceImpl avoirService;

    private Avoir avoir;

    @BeforeEach
    void setUp() {
        avoir = new Avoir();
        avoir.setId(1);
        avoir.setCode("AVOIR-TEST001");
        avoir.setDateAvoir(Instant.now());
        avoir.setMontant(150.0);
        avoir.setRaison("Produit defectueux");
        avoir.setEtat("EN_ATTENTE");
        avoir.setIdentreprise(1);
    }

    @Test
    void save_shouldGenerateCodeIfNull() {
        AvoirDto dto = AvoirDto.builder()
                .montant(200.0)
                .raison("Retour client")
                .identreprise(1)
                .build();

        when(avoirRepository.save(any(Avoir.class))).thenReturn(avoir);

        AvoirDto result = avoirService.save(dto);

        assertNotNull(result);
    }

    @Test
    void findById_shouldReturnAvoirDto() {
        when(avoirRepository.findById(1)).thenReturn(Optional.of(avoir));

        AvoirDto result = avoirService.findById(1);

        assertNotNull(result);
        assertEquals("AVOIR-TEST001", result.getCode());
        assertEquals(150.0, result.getMontant());
    }

    @Test
    void findByClientId_shouldReturnAvoirs() {
        when(avoirRepository.findByClientIdAndIdentreprise(1, 1)).thenReturn(Arrays.asList(avoir));

        List<AvoirDto> result = avoirService.findByClientId(1, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByEtat_shouldReturnMatchingAvoirs() {
        when(avoirRepository.findByEtatAndIdentreprise("EN_ATTENTE", 1)).thenReturn(Arrays.asList(avoir));

        List<AvoirDto> result = avoirService.findByEtat("EN_ATTENTE", 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EN_ATTENTE", result.get(0).getEtat());
    }

    @Test
    void delete_shouldCallRepository() {
        avoirService.delete(1);
        verify(avoirRepository, times(1)).deleteById(1);
    }
}
