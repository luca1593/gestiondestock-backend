package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.*;
import com.devtech.gestiondestock.services.FactureService;
import com.devtech.gestiondestock.services.PaiementService;
import com.devtech.gestiondestock.validator.FactureValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final FournisseurRepository fournisseurRepository;
    private final VenteRepository venteRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final CompteurFactureRepository compteurFactureRepository;
    private final PaiementService paiementService;

    public FactureServiceImpl(FactureRepository factureRepository,
                             ClientRepository clientRepository,
                             FournisseurRepository fournisseurRepository,
                             VenteRepository venteRepository,
                             CommandeClientRepository commandeClientRepository,
                             CommandeFournisseurRepository commandeFournisseurRepository,
                             CompteurFactureRepository compteurFactureRepository,
                             PaiementService paiementService) {
        this.factureRepository = factureRepository;
        this.clientRepository = clientRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.venteRepository = venteRepository;
        this.commandeClientRepository = commandeClientRepository;
        this.commandeFournisseurRepository = commandeFournisseurRepository;
        this.compteurFactureRepository = compteurFactureRepository;
        this.paiementService = paiementService;
    }

    @Override
    @Transactional
    public FactureDto save(FactureDto dto) {
        List<String> errors = FactureValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Facture is not valid {}", dto);
            throw new InvalidEntityException("La facture n'est pas valide", ErrorsCode.FACTURE_NOT_VALID, errors);
        }
        Facture facture = preparerEntite(dto);
        return FactureDto.fromEntity(factureRepository.save(facture));
    }

    @Override
    @Transactional
    public FactureDto saveAvecPaiement(FactureDto dto) {
        if (dto == null || dto.getPaiement() == null) {
            throw new InvalidEntityException("Le paiement de la facture est obligatoire",
                    ErrorsCode.FACTURE_NOT_VALID,
                    List.of("Veuillez renseigner les informations de paiement de la facture"));
        }
        FactureDto facture = save(dto);

        PaiementDto paiement = dto.getPaiement();
        paiement.setId(null);
        paiement.setFactureId(facture.getId());
        if (paiement.getEntrepriseId() == null) {
            paiement.setEntrepriseId(facture.getEntrepriseId());
        }
        paiementService.save(paiement);

        return findById(facture.getId());
    }

    @Override
    public FactureDto findById(Integer id) {
        return factureRepository.findById(id)
                .map(FactureDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Facture non trouvée avec l'ID " + id, ErrorsCode.FACTURE_NOT_FOUND));
    }

    @Override
    public FactureDto findByNumeroFacture(String numeroFacture) {
        Facture facture = factureRepository.findByNumeroFacture(numeroFacture);
        if (facture == null) throw new EntityNotFoundException("Facture non trouvée avec le numéro " + numeroFacture, ErrorsCode.FACTURE_NOT_FOUND);
        return FactureDto.fromEntity(facture);
    }

    @Override
    public List<FactureDto> findAll() {
        return factureRepository.findAll().stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<FactureDto> findByClient(Integer clientId) {
        return factureRepository.findByClientId(clientId).stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<FactureDto> findByFournisseur(Integer fournisseurId) {
        return factureRepository.findByFournisseurId(fournisseurId).stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<FactureDto> findByEntreprise(Integer entrepriseId) {
        return factureRepository.findByEntrepriseId(entrepriseId).stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<FactureDto> findByStatut(Facture.StatutFacture statut) {
        return factureRepository.findByStatut(statut).stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<FactureDto> findByType(Facture.TypeFacture typeFacture) {
        return factureRepository.findByTypeFacture(typeFacture).stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<FactureDto> findBySource(Facture.SourceDocument sourceType, Integer sourceId) {
        return factureRepository.findBySourceTypeAndSourceId(sourceType, sourceId).stream()
                .map(FactureDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        factureRepository.deleteById(id);
    }

    /**
     * Construit l'entite facture en resolvant les relations (client/fournisseur),
     * le document source polymorphe, l'entreprise courante et le numero de facture.
     */
    private Facture preparerEntite(FactureDto dto) {
        Facture facture = FactureDto.toEntity(dto);

        Integer entrepriseId = dto.getEntrepriseId() != null ? dto.getEntrepriseId() : entrepriseCourante();
        if (entrepriseId != null) {
            Entreprise entreprise = new Entreprise();
            entreprise.setId(entrepriseId);
            facture.setEntreprise(entreprise);
        }

        Facture.TypeFacture type = dto.getTypeFacture() != null ? dto.getTypeFacture() : Facture.TypeFacture.CLIENT;
        facture.setTypeFacture(type);

        if (type == Facture.TypeFacture.FOURNISSEUR) {
            verifierFournisseur(dto.getFournisseurId(), entrepriseId);
            Fournisseur fournisseur = new Fournisseur();
            fournisseur.setId(dto.getFournisseurId());
            facture.setFournisseur(fournisseur);
            facture.setClient(null);
        } else {
            verifierClient(dto.getClientId(), entrepriseId);
            Client client = new Client();
            client.setId(dto.getClientId());
            facture.setClient(client);
            facture.setFournisseur(null);
        }

        if (dto.getSourceType() != null && dto.getSourceId() != null) {
            verifierDocumentSource(dto.getSourceType(), dto.getSourceId(), entrepriseId);
            facture.setSourceType(dto.getSourceType());
            facture.setSourceId(dto.getSourceId());
        }

        if (!StringUtils.hasLength(dto.getNumeroFacture())) {
            if (entrepriseId == null) {
                throw new InvalidEntityException("Impossible de generer le numero de facture sans entreprise",
                        ErrorsCode.FACTURE_NOT_VALID,
                        List.of("Aucune entreprise associee a la facture"));
            }
            facture.setNumeroFacture(genererNumero(entrepriseId));
        }

        if (facture.getMontantPaye() == null) {
            facture.setMontantPaye(BigDecimal.ZERO);
        }
        if (facture.getMontantRestant() == null && facture.getMontantTTC() != null) {
            facture.setMontantRestant(facture.getMontantTTC().subtract(facture.getMontantPaye()));
        }
        if (facture.getStatut() == null) {
            facture.setStatut(Facture.StatutFacture.EN_ATTENTE);
        }
        return facture;
    }

    /**
     * Genere un numero de facture sequentiel par entreprise et par annee, de facon
     * atomique grace au verrou pessimiste du compteur.
     */
    public String genererNumero(Integer entrepriseId) {
        int annee = LocalDate.now().getYear();
        CompteurFacture compteur = compteurFactureRepository.findPourMiseAJour(entrepriseId, annee)
                .orElseGet(() -> {
                    CompteurFacture nouveau = new CompteurFacture();
                    nouveau.setIdentreprise(entrepriseId);
                    nouveau.setAnnee(annee);
                    nouveau.setValeur(0L);
                    return nouveau;
                });
        compteur.setValeur(compteur.getValeur() + 1);
        compteurFactureRepository.save(compteur);
        return String.format("FAC-%d-%d-%06d", entrepriseId, annee, compteur.getValeur());
    }

    private Integer entrepriseCourante() {
        String idEntreprise = MDC.get("idEntreprise");
        if (StringUtils.hasLength(idEntreprise)) {
            try {
                return Integer.parseInt(idEntreprise);
            } catch (NumberFormatException e) {
                log.warn("Identifiant d'entreprise invalide dans le contexte : {}", idEntreprise);
            }
        }
        return null;
    }

    private void verifierClient(Integer clientId, Integer entrepriseId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new InvalidEntityException("Le client n'existe pas dans la BDD",
                        ErrorsCode.FACTURE_NOT_VALID,
                        List.of("Aucun client avec l'ID = " + clientId)));
        verifierAppartenanceEntreprise(client.getIdentreprise(), entrepriseId, "client", clientId);
    }

    private void verifierFournisseur(Integer fournisseurId, Integer entrepriseId) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new InvalidEntityException("Le fournisseur n'existe pas dans la BDD",
                        ErrorsCode.FACTURE_NOT_VALID,
                        List.of("Aucun fournisseur avec l'ID = " + fournisseurId)));
        verifierAppartenanceEntreprise(fournisseur.getIdentreprise(), entrepriseId, "fournisseur", fournisseurId);
    }

    private void verifierDocumentSource(Facture.SourceDocument sourceType, Integer sourceId, Integer entrepriseId) {
        List<String> errors = new ArrayList<>();
        switch (sourceType) {
            case VENTE -> venteRepository.findById(sourceId).ifPresentOrElse(
                    vente -> verifierAppartenanceEntreprise(vente.getIdentreprise(), entrepriseId, "vente", sourceId),
                    () -> errors.add("Aucune vente avec l'ID = " + sourceId));
            case COMMANDE_CLIENT -> commandeClientRepository.findById(sourceId).ifPresentOrElse(
                    commande -> verifierAppartenanceEntreprise(commande.getIdentreprise(), entrepriseId, "commande client", sourceId),
                    () -> errors.add("Aucune commande client avec l'ID = " + sourceId));
            case COMMANDE_FOURNISSEUR -> commandeFournisseurRepository.findById(sourceId).ifPresentOrElse(
                    commande -> verifierAppartenanceEntreprise(commande.getIdentreprise(), entrepriseId, "commande fournisseur", sourceId),
                    () -> errors.add("Aucune commande fournisseur avec l'ID = " + sourceId));
        }
        if (!errors.isEmpty()) {
            throw new InvalidEntityException("Le document source de la facture est invalide",
                    ErrorsCode.FACTURE_NOT_VALID, errors);
        }
    }

    private void verifierAppartenanceEntreprise(Integer identrepriseDocument, Integer entrepriseId, String libelle, Integer documentId) {
        if (entrepriseId != null && identrepriseDocument != null && !entrepriseId.equals(identrepriseDocument)) {
            throw new InvalidEntityException("Le document n'appartient pas a l'entreprise courante",
                    ErrorsCode.FACTURE_NOT_VALID,
                    List.of("Le " + libelle + " avec l'ID = " + documentId + " n'appartient pas a l'entreprise " + entrepriseId));
        }
    }
}
