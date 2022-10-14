package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.dto.RoleDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.repository.EntrepriseRepository;
import com.devtech.gestiondestock.repository.RoleRepository;
import com.devtech.gestiondestock.services.EntrepriseService;
import com.devtech.gestiondestock.services.UtilisateurService;
import com.devtech.gestiondestock.validator.EntrepriseValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EntrepriseServiceImpl implements EntrepriseService {

    private EntrepriseRepository entrepriseRepository;
    private UtilisateurService utilisateurService;
    private RoleRepository roleRepository;

    @Autowired
    public EntrepriseServiceImpl(EntrepriseRepository entrepriseRepository, UtilisateurService utilisateurService, RoleRepository roleRepository) {
        this.entrepriseRepository = entrepriseRepository;
        this.utilisateurService = utilisateurService;
        this.roleRepository = roleRepository;
    }

    @Override
    public EntrepriseDto save(EntrepriseDto dto) {
        List<String> errors = EntrepriseValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Entreprise is invalid {}");
            throw new InvalidEntityException("L'entreprise n'est pas valide", ErrorsCode.ENTREPRISE_NOT_VALID, errors);
        }
        EntrepriseDto savedEntreprise =  EntrepriseDto.fromEntity(
                entrepriseRepository.save(EntrepriseDto.toEntity(dto))
        );

        UtilisateurDto utilisateur = fromEntreprise(savedEntreprise);
        utilisateur.setEntreprise(savedEntreprise);
        UtilisateurDto savedUtilisateur = utilisateurService.save(utilisateur);

        RoleDto roleDto = RoleDto.builder()
                .roleNom("Admin")
                .utilisateur(savedUtilisateur)
                .build();

        roleRepository.save(RoleDto.toEntity(roleDto));

        return savedEntreprise;
    }

    private UtilisateurDto fromEntreprise(EntrepriseDto dto){
        return UtilisateurDto.builder()
                .adresse(dto.getAdresse())
                .nom(dto.getNom())
                .prenom(dto.getCodeFiscal())
                .email(dto.getEmail())
                .motDePasse(generateRandomPassword())
                .entreprise(dto)
                .dateDeNaissance(Instant.now())
                .photo(dto.getPhoto())
                .build();
    }

    private String generateRandomPassword(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode("s0n3R@nd0mP@$$w0rd");
    }

    @Override
    public EntrepriseDto findById(Integer id) {
        if (id == null){
            log.error("Entreprise ID is null");
            return null;
        }
        Optional<Entreprise> entreprise = entrepriseRepository.findById(id);
        return Optional.of(EntrepriseDto.fromEntity(entreprise.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun Entreprise trouver avec l'id = " + id + " dans la BDD",
                        ErrorsCode.ENTREPRISE_NOT_FOUND
                )
        );
    }

    @Override
    public EntrepriseDto findbyNomEntreprise(String nom) {
        if (!StringUtils.hasLength(nom)){
            log.error("Entreprise nom is null");
            return null;
        }
        Optional<Entreprise> entreprise = entrepriseRepository.findEntrepriseByNom(nom);
        return Optional.of(EntrepriseDto.fromEntity(entreprise.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun entreprise trouver avec le nom  = " + nom + " dans la BDD",
                        ErrorsCode.ENTREPRISE_NOT_FOUND
                )
        );
    }

    @Override
    public EntrepriseDto findbyEmailEntreprise(String email) {
        if (!StringUtils.hasLength(email)){
            log.error("Entreprise email is null");
            return null;
        }
        Optional<Entreprise> entreprise = entrepriseRepository.findEntrepriseByEmail(email);
        return Optional.of(EntrepriseDto.fromEntity(entreprise.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun entreprise trouver avec l'email  = " + email + " dans la BDD",
                        ErrorsCode.ENTREPRISE_NOT_FOUND
                )
        );
    }

    @Override
    public List<EntrepriseDto> findAll() {
        return entrepriseRepository.findAll().stream()
                .map(EntrepriseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("Entreprise ID is null");
            return;
        }
        entrepriseRepository.deleteById(id);
    }
}
