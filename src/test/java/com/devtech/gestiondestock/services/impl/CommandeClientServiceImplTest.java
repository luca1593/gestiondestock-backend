package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.CommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.model.LigneCommandeClient;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.ClientRepository;
import com.devtech.gestiondestock.repository.CommandeClientRepository;
import com.devtech.gestiondestock.repository.LigneCommandeClientRepository;
import com.devtech.gestiondestock.services.MvtStkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link CommandeClientServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class CommandeClientServiceImplTest {

    @Mock
    private CommandeClientRepository commandeClientRepository;
    @Mock
    private LigneCommandeClientRepository ligneCommandeClientRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private MvtStkService mvtStkService;

    @InjectMocks
    private CommandeClientServiceImpl commandeClientService;

    private Client client;
    private Article article;
    private CommandeClient commandeClient;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1);
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean@test.com");

        Category category = new Category();
        category.setId(1);
        category.setCode("CAT-001");
        category.setDesignation("Catégorie test");

        article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Article test");
        article.setPrixUnitaireht(BigDecimal.valueOf(10));
        article.setTauxTva(BigDecimal.valueOf(20));
        article.setPrixTtc(BigDecimal.valueOf(12));
        article.setCategory(category);
        article.setStock(BigDecimal.valueOf(100));

        commandeClient = new CommandeClient();
        commandeClient.setId(1);
        commandeClient.setCode("CC-001");
        commandeClient.setDateCommande(Instant.now());
        commandeClient.setEtatcommande(EtatCommande.EN_PREPARATION);
        commandeClient.setClient(client);
        commandeClient.setIdentreprise(1);

        lenient().when(commandeClientRepository.save(any(CommandeClient.class))).thenAnswer(inv -> {
            CommandeClient saved = inv.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(10);
            }
            return saved;
        });
        lenient().when(ligneCommandeClientRepository.save(any(LigneCommandeClient.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    private AdresseDto adresseValide() {
        return AdresseDto.builder()
                .adresse1("1 rue test")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private CommandeClientDto dtoValide() {
        return CommandeClientDto.builder()
                .code("CC-001")
                .dateCommande(Instant.now())
                .etatcommande(EtatCommande.EN_PREPARATION)
                .client(ClientDto.builder().id(1).build())
                .identreprise(1)
                .ligneCommandeClients(Arrays.asList(LigneCommandeClientDto.builder()
                        .article(ArticleDto.builder().id(1).build())
                        .quantite(BigDecimal.valueOf(2))
                        .prixUnitaire(BigDecimal.valueOf(10))
                        .build()))
                .build();
    }

    private LigneCommandeClient ligneCommandeClient() {
        LigneCommandeClient ligne = new LigneCommandeClient();
        ligne.setId(1);
        ligne.setArticle(article);
        ligne.setQuantite(BigDecimal.valueOf(2));
        ligne.setPrixUnitaire(BigDecimal.valueOf(10));
        ligne.setIdentreprise(1);
        return ligne;
    }

    // ---------------------------------------------------------------------
    // save
    // ---------------------------------------------------------------------

    @Test
    void save_quandDtoInvalide_leveInvalidEntityException_etRepositoryJamaisAppele() {
        assertThrows(InvalidEntityException.class,
                () -> commandeClientService.save(CommandeClientDto.builder().build()));

        verify(commandeClientRepository, never()).save(any());
    }

    @Test
    void save_quandClientInexistant_leveEntityNotFoundException() {
        CommandeClientDto dto = dtoValide();
        dto.setClient(ClientDto.builder().id(99).build());
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandeClientService.save(dto));

        verify(commandeClientRepository, never()).save(any());
    }

    @Test
    void save_quandArticleNullDansLigne_leveInvalidEntityException() {
        CommandeClientDto dto = dtoValide();
        dto.getLigneCommandeClients().get(0).setArticle(null);
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));

        assertThrows(InvalidEntityException.class, () -> commandeClientService.save(dto));

        verify(commandeClientRepository, never()).save(any());
    }

    @Test
    void save_quandArticleInexistant_leveInvalidEntityException() {
        CommandeClientDto dto = dtoValide();
        dto.getLigneCommandeClients().get(0).setArticle(ArticleDto.builder().id(99).build());
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(articleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(InvalidEntityException.class, () -> commandeClientService.save(dto));

        verify(commandeClientRepository, never()).save(any());
    }

    @Test
    void save_quandCommandeLivree_leveInvalidOpperatioException() {
        CommandeClientDto dto = dtoValide();
        dto.setId(1);
        dto.setEtatcommande(EtatCommande.LIVREE);

        assertThrows(InvalidOpperatioException.class, () -> commandeClientService.save(dto));

        verify(commandeClientRepository, never()).save(any());
        verify(clientRepository, never()).findById(any());
    }

    @Test
    void save_quandValide_sauvegardeCommandeEtLignes_etEnregistreMouvementStock() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(ligneCommandeClientRepository.findAllByCommandeClientId(10))
                .thenReturn(Arrays.asList(ligneCommandeClient()));

        CommandeClientDto result = commandeClientService.save(dtoValide());

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("CC-001", result.getCode());
        verify(ligneCommandeClientRepository, times(1)).save(any(LigneCommandeClient.class));
        verify(mvtStkService, times(1)).sortieMvtStk(any(MvtStkDto.class));
    }

    // ---------------------------------------------------------------------
    // findById / findByCodeCommande / findByDateCommande
    // ---------------------------------------------------------------------

    @Test
    void findById_quandIdNull_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class, () -> commandeClientService.findById(null));
        verify(commandeClientRepository, never()).findById(any());
    }

    @Test
    void findById_quandInexistant_leveEntityNotFoundException() {
        when(commandeClientRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandeClientService.findById(999));
    }

    @Test
    void findById_quandExistant_retourneDto() {
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));

        CommandeClientDto result = commandeClientService.findById(1);

        assertNotNull(result);
        assertEquals("CC-001", result.getCode());
    }

    @Test
    void findByCodeCommande_quandCodeVide_retourneNull() {
        assertNull(commandeClientService.findByCodeCommande(""));
        verify(commandeClientRepository, never()).findCommandeClientByCode(any());
    }

    @Test
    void findByCodeCommande_quandInexistant_leveEntityNotFoundException() {
        when(commandeClientRepository.findCommandeClientByCode("INCONNU")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commandeClientService.findByCodeCommande("INCONNU"));
    }

    @Test
    void findByDateCommande_quandDateNull_retourneListeVide() {
        List<CommandeClientDto> result = commandeClientService.findByDateCommande(null);

        assertTrue(result.isEmpty());
        verify(commandeClientRepository, never()).findAllByDateCommande(any());
    }

    @Test
    void findAll_retourneListe() {
        when(commandeClientRepository.findAll()).thenReturn(Arrays.asList(commandeClient));

        List<CommandeClientDto> result = commandeClientService.findAll();

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------------------
    // updateEtatCommande
    // ---------------------------------------------------------------------

    @Test
    void updateEtatCommande_quandCommandeLivree_leveInvalidOpperatioException() {
        CommandeClient livree = new CommandeClient();
        livree.setId(1);
        livree.setEtatcommande(EtatCommande.LIVREE);
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(livree));

        assertThrows(InvalidOpperatioException.class,
                () -> commandeClientService.updateEtatCommande(1, EtatCommande.VALIDEE));

        verify(commandeClientRepository, never()).save(any());
    }

    @Test
    void updateEtatCommande_quandValide_enregistreLeNouvelEtat() {
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));

        CommandeClientDto result = commandeClientService.updateEtatCommande(1, EtatCommande.VALIDEE);

        assertNotNull(result);
        assertEquals(EtatCommande.VALIDEE, result.getEtatcommande());
    }

    @Test
    void updateEtatCommande_versLivree_enregistreMouvementStock() {
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));
        when(ligneCommandeClientRepository.findAllByCommandeClientId(1))
                .thenReturn(Arrays.asList(ligneCommandeClient()));

        commandeClientService.updateEtatCommande(1, EtatCommande.LIVREE);

        verify(mvtStkService, times(1)).sortieMvtStk(any(MvtStkDto.class));
    }

    // ---------------------------------------------------------------------
    // updateQuantiterCommande
    // ---------------------------------------------------------------------

    @Test
    void updateQuantiterCommande_quandQuantiteZero_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class,
                () -> commandeClientService.updateQuantiterCommande(1, 1, BigDecimal.ZERO));

        verify(commandeClientRepository, never()).findById(any());
    }

    @Test
    void updateQuantiterCommande_quandValide_metAJourLaLigne() {
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));
        when(ligneCommandeClientRepository.findById(1)).thenReturn(Optional.of(ligneCommandeClient()));

        CommandeClientDto result = commandeClientService.updateQuantiterCommande(1, 1, BigDecimal.valueOf(7));

        assertNotNull(result);
        verify(ligneCommandeClientRepository).save(any(LigneCommandeClient.class));
    }

    // ---------------------------------------------------------------------
    // updateClient / updateArticle / deleteArticle
    // ---------------------------------------------------------------------

    @Test
    void updateClient_quandClientInexistant_leveEntityNotFoundException() {
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandeClientService.updateClient(1, 99));
    }

    @Test
    void updateClient_quandValide_metAJourLeClient() {
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));
        when(clientRepository.findById(2)).thenReturn(Optional.of(client));

        CommandeClientDto result = commandeClientService.updateClient(1, 2);

        assertNotNull(result);
        verify(commandeClientRepository).save(any(CommandeClient.class));
    }

    @Test
    void updateArticle_quandArticleInexistant_leveEntityNotFoundException() {
        when(articleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commandeClientService.updateArticle(1, 1, 99));

        verify(commandeClientRepository, never()).findById(any());
    }

    @Test
    void updateArticle_quandValide_metAJourLaLigne() {
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));
        when(ligneCommandeClientRepository.findById(1)).thenReturn(Optional.of(ligneCommandeClient()));

        CommandeClientDto result = commandeClientService.updateArticle(1, 1, 1);

        assertNotNull(result);
        verify(ligneCommandeClientRepository).save(any(LigneCommandeClient.class));
    }

    @Test
    void deleteArticle_quandValide_supprimeLaLigne() {
        when(commandeClientRepository.findById(1)).thenReturn(Optional.of(commandeClient));
        when(ligneCommandeClientRepository.findById(1)).thenReturn(Optional.of(ligneCommandeClient()));

        commandeClientService.deleteArticle(1, 1);

        verify(ligneCommandeClientRepository, times(1)).deleteById(1);
    }

    // ---------------------------------------------------------------------
    // findAllByCommandeClient / findAllByClientDto
    // ---------------------------------------------------------------------

    @Test
    void findAllByCommandeClient_quandIdNull_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class,
                () -> commandeClientService.findAllByCommandeClient(null));

        verify(ligneCommandeClientRepository, never()).findAllByCommandeClientId(any());
    }

    @Test
    void findAllByCommandeClient_quandValide_retourneLignes() {
        when(ligneCommandeClientRepository.findAllByCommandeClientId(1))
                .thenReturn(Arrays.asList(ligneCommandeClient()));

        List<LigneCommandeClientDto> result = commandeClientService.findAllByCommandeClient(1);

        assertEquals(1, result.size());
    }

    @Test
    void findAllByClientDto_quandClientInvalide_leveInvalidEntityException() {
        assertThrows(InvalidEntityException.class,
                () -> commandeClientService.findAllByClientDto(ClientDto.builder().build()));

        verify(commandeClientRepository, never()).findAllByClient(any());
    }

    @Test
    void findAllByClientDto_quandValide_retourneListe() {
        when(commandeClientRepository.findAllByClient(any(Client.class)))
                .thenReturn(Arrays.asList(commandeClient));

        ClientDto clientDto = ClientDto.builder()
                .nom("Dupont").prenom("Jean").email("jean@test.com").numTel("0102030405")
                .adresse(adresseValide())
                .build();

        List<CommandeClientDto> result = commandeClientService.findAllByClientDto(clientDto);

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------------------
    // delete
    // ---------------------------------------------------------------------

    @Test
    void delete_quandIdNull_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class, () -> commandeClientService.delete(null));

        verify(commandeClientRepository, never()).deleteById(any());
    }

    @Test
    void delete_appelleRepository() {
        commandeClientService.delete(1);

        verify(commandeClientRepository, times(1)).deleteById(1);
    }
}
