package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AlertStockDto;
import com.devtech.gestiondestock.model.AlertStock;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.repository.AlertStockRepository;
import com.devtech.gestiondestock.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertStockServiceImplTest {

    @Mock
    private AlertStockRepository alertStockRepository;
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private AlertStockServiceImpl alertStockService;

    private AlertStock alert;

    @BeforeEach
    void setUp() {
        alert = new AlertStock();
        alert.setId(1);
        alert.setArticleId(1);
        alert.setDesignation("Article Test");
        alert.setStockActuel(3.0);
        alert.setSeuilMinimum(5.0);
        alert.setSeuilCritique(0.0);
        alert.setNiveauAlerte("BAS");
        alert.setActive(true);
        alert.setIdentreprise(1);
    }

    @Test
    void findById_shouldReturnAlertStockDto() {
        when(alertStockRepository.findById(1)).thenReturn(Optional.of(alert));

        AlertStockDto result = alertStockService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("BAS", result.getNiveauAlerte());
    }

    @Test
    void findAlertesActives_shouldReturnActiveAlerts() {
        when(alertStockRepository.findByIdentrepriseAndActiveTrue(1)).thenReturn(Arrays.asList(alert));

        List<AlertStockDto> result = alertStockService.findAlertesActives(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BAS", result.get(0).getNiveauAlerte());
    }

    @Test
    void findByNiveauAlerte_shouldReturnMatchingAlerts() {
        when(alertStockRepository.findByNiveauAlerte("CRITIQUE")).thenReturn(Collections.emptyList());

        List<AlertStockDto> result = alertStockService.findByNiveauAlerte("CRITIQUE");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_shouldCallRepository() {
        alertStockService.delete(1);
        verify(alertStockRepository, times(1)).deleteById(1);
    }
}
