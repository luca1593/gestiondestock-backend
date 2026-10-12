package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.model.Paiement;
import com.devtech.gestiondestock.repository.FactureRepository;
import com.devtech.gestiondestock.repository.PaiementRepository;
import com.devtech.gestiondestock.services.PaiementService;
import com.devtech.gestiondestock.validator.PaiementValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;

    public PaiementServiceImpl(PaiementRepository paiementRepository, FactureRepository factureRepository) {
        this.paiementRepository = paiementRepository;
        this.factureRepository = factureRepository;
    }

    @Override
    @Transactional
    public PaiementDto save(PaiementDto dto) {
        List<String> errors = PaiementValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Paiement is not valid {}", dto);
            throw new InvalidEntityException("Le paiement n'est pas valide", ErrorsCode.PAIEMENT_NOT_VALID, errors);
        }
        Paiement paiement = PaiementDto.toEntity(dto);

        Facture facture = factureRepository.findById(dto.getFactureId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Facture non trouvée avec l'ID " + dto.getFactureId(), ErrorsCode.FACTURE_NOT_FOUND));
        paiement.setFacture(facture);

        if (paiement.getEntreprise() == null) {
            Integer entrepriseId = dto.getEntrepriseId() != null ? dto.getEntrepriseId() : facture.getEntreprise() != null
                    ? facture.getEntreprise().getId() : entrepriseCourante();
            if (entrepriseId != null) {
                Entreprise entreprise = new Entreprise();
                entreprise.setId(entrepriseId);
                paiement.setEntreprise(entreprise);
            }
        }

        // Monnaie rendue : uniquement pertinente pour un paiement en especes, et
        // toujours recalculee cote serveur pour rester coherente avec le montant.
        if (paiement.getModePaiement() == Paiement.ModePaiement.ESPECES
                && paiement.getMontantDonne() != null
                && paiement.getMontant() != null) {
            paiement.setMonnaie(paiement.getMontantDonne().subtract(paiement.getMontant()));
        } else {
            paiement.setMonnaie(null);
        }

        Paiement saved = paiementRepository.save(paiement);
        recalculerFacture(saved.getFacture());
        return PaiementDto.fromEntity(saved);
    }

    @Override
    public PaiementDto findById(Integer id) {
        return paiementRepository.findById(id)
                .map(PaiementDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé avec l'ID " + id, ErrorsCode.PAIEMENT_NOT_FOUND));
    }

    @Override
    public List<PaiementDto> findAll() {
        return paiementRepository.findAll().stream().map(PaiementDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<PaiementDto> findByFacture(Integer factureId) {
        return paiementRepository.findByFactureId(factureId).stream().map(PaiementDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<PaiementDto> findByModePaiement(Paiement.ModePaiement modePaiement) {
        return paiementRepository.findByModePaiement(modePaiement).stream().map(PaiementDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Paiement paiement = paiementRepository.findById(id).orElse(null);
        Integer factureId = paiement != null && paiement.getFacture() != null ? paiement.getFacture().getId() : null;
        paiementRepository.deleteById(id);
        if (factureId != null) {
            factureRepository.findById(factureId).ifPresent(this::mettreAJourTotaux);
        }
    }

    /**
     * Recalcule le montant paye, le montant restant et le statut de la facture
     * a partir de l'ensemble de ses paiements.
     */
    private void recalculerFacture(Facture facture) {
        if (facture == null || facture.getId() == null) {
            return;
        }
        factureRepository.findById(facture.getId()).ifPresent(this::mettreAJourTotaux);
    }

    private void mettreAJourTotaux(Facture facture) {
        BigDecimal total = paiementRepository.findByFactureId(facture.getId()).stream()
                .map(Paiement::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTTC = facture.getMontantTTC() != null ? facture.getMontantTTC() : BigDecimal.ZERO;
        facture.setMontantPaye(total);
        facture.setMontantRestant(montantTTC.subtract(total));

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            facture.setStatut(Facture.StatutFacture.EN_ATTENTE);
        } else if (total.compareTo(montantTTC) >= 0) {
            facture.setStatut(Facture.StatutFacture.PAYEE);
        } else {
            facture.setStatut(Facture.StatutFacture.PARTIELLEMENT_PAYEE);
        }
        factureRepository.save(facture);
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
}
