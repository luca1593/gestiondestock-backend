package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.model.Fournisseur;
import com.devtech.gestiondestock.model.Paiement;
import com.devtech.gestiondestock.model.Vente;
import com.devtech.gestiondestock.repository.ClientRepository;
import com.devtech.gestiondestock.repository.FournisseurRepository;
import com.devtech.gestiondestock.repository.VenteRepository;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FactureEncaissementTest {

    @Autowired
    private FactureService factureService;

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private VenteRepository venteRepository;

    private Integer clientId;
    private Integer fournisseurId;
    private Integer venteId;

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");

        Client client = new Client();
        client.setNom("Client Encaissement");
        client.setEmail("client-encaissement@test.com");
        client.setIdentreprise(1);
        clientId = clientRepository.save(client).getId();

        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNom("Fournisseur Encaissement");
        fournisseur.setEmail("fournisseur-encaissement@test.com");
        fournisseur.setIdentreprise(1);
        fournisseurId = fournisseurRepository.save(fournisseur).getId();

        Vente vente = new Vente();
        vente.setCode("V-ENC-001");
        vente.setDateVente(Instant.now());
        vente.setIdentreprise(1);
        venteId = venteRepository.save(vente).getId();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    private FactureDto factureClient() {
        return FactureDto.builder()
                .dateFacture(LocalDateTime.now())
                .montantHT(BigDecimal.valueOf(1000))
                .montantTVA(BigDecimal.valueOf(200))
                .montantTTC(BigDecimal.valueOf(1200))
                .clientId(clientId)
                .build();
    }

    @Test
    void shouldGenerateNumeroWhenAbsent() {
        FactureDto saved = factureService.save(factureClient());

        assertNotNull(saved.getNumeroFacture());
        assertTrue(saved.getNumeroFacture().startsWith("FAC-1-"));
        assertEquals(Facture.TypeFacture.CLIENT, saved.getTypeFacture());
        assertEquals(Facture.StatutFacture.EN_ATTENTE, saved.getStatut());
        assertEquals(0, BigDecimal.ZERO.compareTo(saved.getMontantPaye()));
        assertEquals(0, BigDecimal.valueOf(1200).compareTo(saved.getMontantRestant()));
    }

    @Test
    void shouldSaveFactureFournisseur() {
        FactureDto saved = factureService.save(FactureDto.builder()
                .dateFacture(LocalDateTime.now())
                .montantHT(BigDecimal.valueOf(500))
                .montantTTC(BigDecimal.valueOf(600))
                .typeFacture(Facture.TypeFacture.FOURNISSEUR)
                .fournisseurId(fournisseurId)
                .build());

        assertEquals(Facture.TypeFacture.FOURNISSEUR, saved.getTypeFacture());
        assertEquals(fournisseurId, saved.getFournisseurId());
        assertNull(saved.getClientId());
    }

    @Test
    void shouldLinkFactureToVente() {
        FactureDto saved = factureService.save(FactureDto.builder()
                .dateFacture(LocalDateTime.now())
                .montantHT(BigDecimal.valueOf(100))
                .montantTTC(BigDecimal.valueOf(120))
                .clientId(clientId)
                .sourceType(Facture.SourceDocument.VENTE)
                .sourceId(venteId)
                .build());

        assertEquals(Facture.SourceDocument.VENTE, saved.getSourceType());
        assertEquals(venteId, saved.getSourceId());
        assertEquals(1, factureService.findBySource(Facture.SourceDocument.VENTE, venteId).size());
    }

    @Test
    void shouldThrowWhenSourceDoesNotExist() {
        assertThrows(InvalidEntityException.class, () -> factureService.save(FactureDto.builder()
                .dateFacture(LocalDateTime.now())
                .montantHT(BigDecimal.ONE)
                .montantTTC(BigDecimal.ONE)
                .clientId(clientId)
                .sourceType(Facture.SourceDocument.VENTE)
                .sourceId(-999)
                .build()));
    }

    @Test
    void shouldEncaisserEspecesAvecMonnaie() {
        FactureDto facture = factureClient();
        facture.setPaiement(PaiementDto.builder()
                .montant(BigDecimal.valueOf(1200))
                .montantDonne(BigDecimal.valueOf(1500))
                .datePaiement(LocalDateTime.now())
                .modePaiement(Paiement.ModePaiement.ESPECES)
                .build());

        FactureDto saved = factureService.saveAvecPaiement(facture);

        assertEquals(Facture.StatutFacture.PAYEE, saved.getStatut());
        assertEquals(0, BigDecimal.valueOf(1200).compareTo(saved.getMontantPaye()));

        List<PaiementDto> paiements = paiementService.findByFacture(saved.getId());
        assertEquals(1, paiements.size());
        assertEquals(0, BigDecimal.valueOf(300).compareTo(paiements.get(0).getMonnaie()));
    }

    @Test
    void shouldRefuserEspecesInsuffisant() {
        FactureDto facture = factureClient();
        facture.setPaiement(PaiementDto.builder()
                .montant(BigDecimal.valueOf(1200))
                .montantDonne(BigDecimal.valueOf(1000))
                .datePaiement(LocalDateTime.now())
                .modePaiement(Paiement.ModePaiement.ESPECES)
                .build());

        assertThrows(InvalidEntityException.class, () -> factureService.saveAvecPaiement(facture));
    }

    @Test
    void shouldRefuserCarteSansReference() {
        FactureDto facture = factureClient();
        facture.setPaiement(PaiementDto.builder()
                .montant(BigDecimal.valueOf(600))
                .datePaiement(LocalDateTime.now())
                .modePaiement(Paiement.ModePaiement.CARTE_BANCAIRE)
                .build());

        assertThrows(InvalidEntityException.class, () -> factureService.saveAvecPaiement(facture));
    }

    @Test
    void shouldRecalculerStatutPartielPuisPaye() {
        FactureDto facture = factureService.save(factureClient());

        paiementService.save(PaiementDto.builder()
                .montant(BigDecimal.valueOf(500))
                .datePaiement(LocalDateTime.now())
                .modePaiement(Paiement.ModePaiement.MOBILE_MONEY)
                .reference("MOB-1")
                .factureId(facture.getId())
                .build());
        assertEquals(Facture.StatutFacture.PARTIELLEMENT_PAYEE, factureService.findById(facture.getId()).getStatut());

        paiementService.save(PaiementDto.builder()
                .montant(BigDecimal.valueOf(700))
                .datePaiement(LocalDateTime.now())
                .modePaiement(Paiement.ModePaiement.MOBILE_MONEY)
                .reference("MOB-2")
                .factureId(facture.getId())
                .build());

        FactureDto apresSolde = factureService.findById(facture.getId());
        assertEquals(Facture.StatutFacture.PAYEE, apresSolde.getStatut());
        assertEquals(0, BigDecimal.ZERO.compareTo(apresSolde.getMontantRestant()));
    }
}
