package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Fournisseur;
import com.devtech.gestiondestock.repository.FournisseurRepository;
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

    @Autowired
    public FournisseurServiceImpl(FournisseurRepository fournisseurRepository){
        this.fournisseurRepository = fournisseurRepository;
    }

    @Override
    public FournisseurDto save(FournisseurDto dto) {
        List<String> errors = FournisseurValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Fournisseur is invalid {}");
            throw new InvalidEntityException("L'entreprise n'est pas valide", ErrorsCode.FOURNISSEUR_NOT_VALID, errors);
        }
        return FournisseurDto.fromEntity(
                fournisseurRepository.save(FournisseurDto.toEntity(dto))
        );
    }

    @Override
    public FournisseurDto findById(Integer id) {
        if (id == null){
            log.error("Fournisseur ID is null");
            return null;
        }
        Optional<Fournisseur> fournisseur = fournisseurRepository.findById(id);
        return Optional.of(FournisseurDto.fromEntity(fournisseur.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun Fournisseur trouver avec l'id = " + id + " dans la BDD",
                        ErrorsCode.FOURNISSEUR_NOT_FOUND
                )
        );
    }

    @Override
    public FournisseurDto findByNomFournisseur(String nom) {
        if (!StringUtils.hasLength(nom)){
            log.error("Fournisseur nom is null");
            return null;
        }
        Optional<Fournisseur> fournisseur = fournisseurRepository.findFournisseurByNom(nom);
        return Optional.of(FournisseurDto.fromEntity(fournisseur.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun Fournisseur trouver avec le nom  = " + nom + " dans la BDD",
                        ErrorsCode.FOURNISSEUR_NOT_FOUND
                )
        );
    }

    @Override
    public FournisseurDto findByEmailFournisseur(String email) {
        if (!StringUtils.hasLength(email)){
            log.error("Fournisseur email is null");
            return null;
        }
        Optional<Fournisseur> fournisseur = fournisseurRepository.findFournisseurByEmail(email);
        return Optional.of(FournisseurDto.fromEntity(fournisseur.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun fournisseur trouver avec l'email  = " + email + " dans la BDD",
                        ErrorsCode.ENTREPRISE_NOT_FOUND
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
        if (!CollectionUtils.isEmpty(dto.getCommandeFournisseurs())) {
            log.error("Fournisseur alredy used");
            throw new InvalidOpperatioException("Operaton impossible : une ou plusieur commande fournisseur existe deja pour ce fournisseur",
                    ErrorsCode.FOURNISSEUR_ALREADY_IN_USE
            );
        }
    }
}
