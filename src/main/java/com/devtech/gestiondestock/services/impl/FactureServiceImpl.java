package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.repository.FactureRepository;
import com.devtech.gestiondestock.services.FactureService;
import com.devtech.gestiondestock.validator.FactureValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FactureServiceImpl implements FactureService {
    private static final Logger log = LoggerFactory.getLogger(FactureServiceImpl.class);
    private final FactureRepository factureRepository;

    public FactureServiceImpl(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    @Override
    public FactureDto save(FactureDto dto) {
        List<String> errors = FactureValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Facture is not valid {}", dto);
            throw new InvalidEntityException("La facture n'est pas valide", ErrorsCode.FACTURE_NOT_VALID, errors);
        }
        Facture facture = FactureDto.toEntity(dto);
        if (dto.getClientId() != null) {
            Client client = new Client();
            client.setId(dto.getClientId());
            facture.setClient(client);
        }
        if (dto.getEntrepriseId() != null) {
            Entreprise e = new Entreprise();
            e.setId(dto.getEntrepriseId());
            facture.setEntreprise(e);
        }
        return FactureDto.fromEntity(factureRepository.save(facture));
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
    public List<FactureDto> findByEntreprise(Integer entrepriseId) {
        return factureRepository.findByEntrepriseId(entrepriseId).stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<FactureDto> findByStatut(Facture.StatutFacture statut) {
        return factureRepository.findByStatut(statut).stream().map(FactureDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        factureRepository.deleteById(id);
    }
}
