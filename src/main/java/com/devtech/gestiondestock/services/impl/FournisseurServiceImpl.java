package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Fournisseur;
import com.devtech.gestiondestock.repository.FournisseurRepository;
import com.devtech.gestiondestock.services.CommandeFournisseurService;
import com.devtech.gestiondestock.services.FournisseurService;
import com.devtech.gestiondestock.validator.FournisseurValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final CommandeFournisseurService cmdFournisseur;

    @Autowired
    public FournisseurServiceImpl(FournisseurRepository fournisseurRepository, CommandeFournisseurService cmdFournisseur){
        this.fournisseurRepository = fournisseurRepository;
        this.cmdFournisseur = cmdFournisseur;
    }

    @Override
    public FournisseurDto save(FournisseurDto dto) {
        List<String> errors = FournisseurValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Fournisseur is invalid: {}", dto);
            throw new InvalidEntityException("Le fournisseur n'est pas valide", ErrorsCode.FOURNISSEUR_NOT_VALID, errors);
        }
        return FournisseurDto.fromEntity(
                fournisseurRepository.save(FournisseurDto.toEntity(dto))
        );
    }

    @Override
    public FournisseurDto findById(Integer id) {
        if (id == null){
            log.error("Fournisseur ID is null");
            throw new EntityNotFoundException("L'ID du fournisseur est null", ErrorsCode.ID_NOT_VALID);
        }
        Optional<Fournisseur> fournisseur = fournisseurRepository.findById(id);
        return fournisseur.map(FournisseurDto::fromEntity).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun fournisseur trouve avec l'id = " + id + " dans la BDD",
                        ErrorsCode.FOURNISSEUR_NOT_FOUND
                )
        );
    }

    @Override
    public FournisseurDto findByNomFournisseur(String nom) {
        if (!StringUtils.hasLength(nom)){
            log.error("Fournisseur nom is null");
            throw new EntityNotFoundException("Aucun fournisseur trouve avec un nom null", ErrorsCode.FOURNISSEUR_NOT_FOUND);
        }
        Optional<Fournisseur> fournisseur = fournisseurRepository.findFournisseurByNom(nom);
        return fournisseur.map(FournisseurDto::fromEntity).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun fournisseur trouve avec le nom = " + nom + " dans la BDD",
                        ErrorsCode.FOURNISSEUR_NOT_FOUND
                )
        );
    }

    @Override
    public FournisseurDto findByEmailFournisseur(String email) {
        if (!StringUtils.hasLength(email)){
            log.error("Fournisseur email is null");
            throw new EntityNotFoundException("Aucun fournisseur trouve avec un email null", ErrorsCode.FOURNISSEUR_NOT_FOUND);
        }
        Optional<Fournisseur> fournisseur = fournisseurRepository.findFournisseurByEmail(email);
        return fournisseur.map(FournisseurDto::fromEntity).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun fournisseur trouve avec l'email = " + email + " dans la BDD",
                        ErrorsCode.FOURNISSEUR_NOT_FOUND
                )
        );
    }

    @Override
    public List<FournisseurDto> findAll() {
        return fournisseurRepository.findAll().stream()
                .map(FournisseurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        checkIdFournisseurBeforeDelete(id);
        fournisseurRepository.deleteById(id);
    }

    private void checkIdFournisseurBeforeDelete(Integer idFournisseur){
        FournisseurDto dto = findById(idFournisseur);
        List<CommandeFournisseurDto> commandeFournisseurDtos = cmdFournisseur.findAllByFournisseurDto(dto);
        if (!CollectionUtils.isEmpty(commandeFournisseurDtos)) {
            log.error("Fournisseur already used");
            throw new InvalidOpperatioException("Operation impossible : une ou plusieurs commandes fournisseur existent deja pour ce fournisseur",
                    ErrorsCode.FOURNISSEUR_ALREADY_IN_USE
            );
        }
    }
}
