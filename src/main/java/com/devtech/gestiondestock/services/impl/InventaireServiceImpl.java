package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.InventaireDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Inventaire;
import com.devtech.gestiondestock.repository.InventaireRepository;
import com.devtech.gestiondestock.services.InventaireService;
import com.devtech.gestiondestock.validator.InventaireValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventaireServiceImpl implements InventaireService {
    private static final Logger log = LoggerFactory.getLogger(InventaireServiceImpl.class);
    private final InventaireRepository inventaireRepository;

    public InventaireServiceImpl(InventaireRepository inventaireRepository) {
        this.inventaireRepository = inventaireRepository;
    }

    @Override
    public InventaireDto save(InventaireDto dto) {
        List<String> errors = InventaireValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Inventaire is not valid {}", dto);
            throw new InvalidEntityException("L'inventaire n'est pas valide", ErrorsCode.INVENTAIRE_NOT_VALID, errors);
        }
        Inventaire inventaire = InventaireDto.toEntity(dto);
        if (dto.getEntrepriseId() != null) {
            Entreprise e = new Entreprise();
            e.setId(dto.getEntrepriseId());
            inventaire.setEntreprise(e);
        }
        if (dto.getEntrepotId() != null) {
            Entrepot e = new Entrepot();
            e.setId(dto.getEntrepotId());
            inventaire.setEntrepot(e);
        }
        return InventaireDto.fromEntity(inventaireRepository.save(inventaire));
    }

    @Override
    public InventaireDto findById(Integer id) {
        return inventaireRepository.findById(id)
                .map(InventaireDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Inventaire non trouvé avec l'ID " + id, ErrorsCode.INVENTAIRE_NOT_FOUND));
    }

    @Override
    public InventaireDto findByCode(String code) {
        Inventaire inv = inventaireRepository.findByCode(code);
        if (inv == null) throw new EntityNotFoundException("Inventaire non trouvé avec le code " + code, ErrorsCode.INVENTAIRE_NOT_FOUND);
        return InventaireDto.fromEntity(inv);
    }

    @Override
    public List<InventaireDto> findAll() {
        return inventaireRepository.findAll().stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<InventaireDto> findByEntreprise(Integer entrepriseId) {
        return inventaireRepository.findByEntrepriseId(entrepriseId).stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<InventaireDto> findByEntrepot(Integer entrepotId) {
        return inventaireRepository.findByEntrepotId(entrepotId).stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<InventaireDto> findByStatut(Inventaire.Statut statut) {
        return inventaireRepository.findByStatut(statut).stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        inventaireRepository.deleteById(id);
    }
}
