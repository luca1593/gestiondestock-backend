package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.model.Paiement;
import com.devtech.gestiondestock.repository.PaiementRepository;
import com.devtech.gestiondestock.services.PaiementService;
import com.devtech.gestiondestock.validator.PaiementValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaiementServiceImpl implements PaiementService {
    private static final Logger log = LoggerFactory.getLogger(PaiementServiceImpl.class);
    private final PaiementRepository paiementRepository;

    public PaiementServiceImpl(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    @Override
    public PaiementDto save(PaiementDto dto) {
        List<String> errors = PaiementValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Paiement is not valid {}", dto);
            throw new InvalidEntityException("Le paiement n'est pas valide", ErrorsCode.PAIEMENT_NOT_VALID, errors);
        }
        Paiement paiement = PaiementDto.toEntity(dto);
        if (dto.getFactureId() != null) {
            Facture facture = new Facture();
            facture.setId(dto.getFactureId());
            paiement.setFacture(facture);
        }
        if (dto.getEntrepriseId() != null) {
            Entreprise entreprise = new Entreprise();
            entreprise.setId(dto.getEntrepriseId());
            paiement.setEntreprise(entreprise);
        }
        return PaiementDto.fromEntity(paiementRepository.save(paiement));
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
    public void delete(Integer id) {
        paiementRepository.deleteById(id);
    }
}
