package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.RegleTarifaireDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.RegleTarifaire;
import com.devtech.gestiondestock.repository.RegleTarifaireRepository;
import com.devtech.gestiondestock.services.RegleTarifaireService;
import com.devtech.gestiondestock.validator.RegleTarifaireValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegleTarifaireServiceImpl implements RegleTarifaireService {
    private static final Logger log = LoggerFactory.getLogger(RegleTarifaireServiceImpl.class);
    private final RegleTarifaireRepository regleTarifaireRepository;

    public RegleTarifaireServiceImpl(RegleTarifaireRepository regleTarifaireRepository) {
        this.regleTarifaireRepository = regleTarifaireRepository;
    }

    @Override
    public RegleTarifaireDto save(RegleTarifaireDto dto) {
        List<String> errors = RegleTarifaireValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("RegleTarifaire is not valid {}", dto);
            throw new InvalidEntityException("La règle tarifaire n'est pas valide", ErrorsCode.REGLE_TARIFAIRE_NOT_VALID, errors);
        }
        RegleTarifaire regle = RegleTarifaireDto.toEntity(dto);
        if (dto.getEntrepriseId() != null) {
            Entreprise e = new Entreprise();
            e.setId(dto.getEntrepriseId());
            regle.setEntreprise(e);
        }
        if (dto.getCategorieId() != null) {
            Category c = new Category();
            c.setId(dto.getCategorieId());
            regle.setCategorie(c);
        }
        if (dto.getClientId() != null) {
            Client c = new Client();
            c.setId(dto.getClientId());
            regle.setClient(c);
        }
        return RegleTarifaireDto.fromEntity(regleTarifaireRepository.save(regle));
    }

    @Override
    public RegleTarifaireDto findById(Integer id) {
        return regleTarifaireRepository.findById(id)
                .map(RegleTarifaireDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Règle tarifaire non trouvée avec l'ID " + id, ErrorsCode.REGLE_TARIFAIRE_NOT_FOUND));
    }

    @Override
    public RegleTarifaireDto findByCode(String code) {
        RegleTarifaire regle = regleTarifaireRepository.findByCode(code);
        if (regle == null) throw new EntityNotFoundException("Règle tarifaire non trouvée avec le code " + code, ErrorsCode.REGLE_TARIFAIRE_NOT_FOUND);
        return RegleTarifaireDto.fromEntity(regle);
    }

    @Override
    public List<RegleTarifaireDto> findAll() {
        return regleTarifaireRepository.findAll().stream().map(RegleTarifaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<RegleTarifaireDto> findByEntreprise(Integer entrepriseId) {
        return regleTarifaireRepository.findByEntrepriseId(entrepriseId).stream().map(RegleTarifaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<RegleTarifaireDto> findByCategorie(Integer categorieId) {
        return regleTarifaireRepository.findByCategorieId(categorieId).stream().map(RegleTarifaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<RegleTarifaireDto> findByTypeRegle(RegleTarifaire.TypeRegle typeRegle) {
        return regleTarifaireRepository.findByTypeRegle(typeRegle).stream().map(RegleTarifaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<RegleTarifaireDto> findActifs() {
        return regleTarifaireRepository.findByEstActifTrue().stream().map(RegleTarifaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        regleTarifaireRepository.deleteById(id);
    }
}
