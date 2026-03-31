package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.DashboardStatsDto;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private FournisseurRepository fournisseurRepository;
    @Mock
    private CommandeClientRepository commandeClientRepository;
    @Mock
    private CommandeFournisseurRepository commandeFournisseurRepository;
    @Mock
    private VenteRepository venteRepository;
    @Mock
    private LigneVenteRepository ligneVenteRepository;
    @Mock
    private LigneCommandeClientRepository ligneCommandeClientRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private List<Article> articles;
    private List<Client> clients;

    @BeforeEach
    void setUp() {
        Article article1 = new Article();
        article1.setId(1);
        article1.setCodeArticle("ART001");
        article1.setDesignation("Article Test 1");
        article1.setStock(BigDecimal.valueOf(10));
        article1.setPrixUnitaireht(BigDecimal.valueOf(100));

        Article article2 = new Article();
        article2.setId(2);
        article2.setCodeArticle("ART002");
        article2.setDesignation("Article Test 2");
        article2.setStock(BigDecimal.valueOf(3));
        article2.setPrixUnitaireht(BigDecimal.valueOf(50));

        articles = Arrays.asList(article1, article2);

        Client client = new Client();
        client.setId(1);
        client.setNom("Test Client");
        clients = Collections.singletonList(client);
    }

    @Test
    void getGlobalStats_shouldReturnCorrectStats() {
        when(articleRepository.findAll()).thenReturn(articles);
        when(clientRepository.findAll()).thenReturn(clients);
        when(fournisseurRepository.findAll()).thenReturn(Collections.emptyList());
        when(commandeClientRepository.findAll()).thenReturn(Collections.emptyList());
        when(commandeFournisseurRepository.findAll()).thenReturn(Collections.emptyList());
        when(venteRepository.findAll()).thenReturn(Collections.emptyList());
        when(ligneVenteRepository.findAll()).thenReturn(Collections.emptyList());

        DashboardStatsDto stats = dashboardService.getGlobalStats(1);

        assertNotNull(stats);
        assertEquals(2, stats.getTotalArticles());
        assertEquals(1, stats.getTotalClients());
        assertEquals(0, stats.getTotalFournisseurs());
        assertEquals(1, stats.getArticlesStockBas());
    }

    @Test
    void getGlobalStats_shouldHandleEmptyData() {
        when(articleRepository.findAll()).thenReturn(Collections.emptyList());
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());
        when(fournisseurRepository.findAll()).thenReturn(Collections.emptyList());
        when(commandeClientRepository.findAll()).thenReturn(Collections.emptyList());
        when(commandeFournisseurRepository.findAll()).thenReturn(Collections.emptyList());
        when(venteRepository.findAll()).thenReturn(Collections.emptyList());
        when(ligneVenteRepository.findAll()).thenReturn(Collections.emptyList());

        DashboardStatsDto stats = dashboardService.getGlobalStats(1);

        assertNotNull(stats);
        assertEquals(0, stats.getTotalArticles());
        assertEquals(0, stats.getTotalClients());
        assertEquals(BigDecimal.ZERO, stats.getValeurStock());
    }
}
