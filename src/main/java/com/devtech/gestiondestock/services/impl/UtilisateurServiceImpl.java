package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ChangerMotDePasseUtilisateurDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Utilisateur;
import com.devtech.gestiondestock.repository.RoleRepository;
import com.devtech.gestiondestock.repository.UtilisateurRepository;
import com.devtech.gestiondestock.services.UtilisateurService;
import com.devtech.gestiondestock.validator.UtilisateurValidator;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private UtilisateurRepository utilisateurRepository;
    private RoleRepository roleRepository;

    @Override
    public UtilisateurDto save(UtilisateurDto dto) {
        List<String> errors = UtilisateurValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Utilisateur is invalid {}");
            throw new InvalidEntityException("L'utilisateur n'est pas valide", ErrorsCode.UTILISATEUR_NOT_VALID,
                    errors);
        }
        return UtilisateurDto.fromEntity(
                utilisateurRepository.save(UtilisateurDto.toEntity(dto)));
    }

    @Override
    public UtilisateurDto findById(Integer id) {
        if (id == null) {
            log.error("Utilisateur ID is null");
            return null;
        }
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        return Optional.of(UtilisateurDto.fromEntity(utilisateur.get())).orElseThrow(() -> new EntityNotFoundException(
                "Aucun utilisateur trouver avec l'id = " + id + " dans la BDD",
                ErrorsCode.UTILISATEUR_NOT_FOUND));
    }

    @Override
    public UtilisateurDto findByNomUtilisateur(String nom) {
        if (!StringUtils.hasLength(nom)) {
            log.error("Utilisateur nom is null");
            return null;
        }
        Optional<Utilisateur> utilisateur = utilisateurRepository.findUtilisateurByNom(nom);
        return Optional.of(UtilisateurDto.fromEntity(utilisateur.get())).orElseThrow(() -> new EntityNotFoundException(
                "Aucun Utilisateur trouver avec le nom  = " + nom + " dans la BDD",
                ErrorsCode.UTILISATEUR_NOT_FOUND));
    }

    @Override
    public UtilisateurDto findByEmailUtilisateur(String email) {
        if (!StringUtils.hasLength(email)) {
            log.error("Utilisateur email is null");
            return null;
        }
        return utilisateurRepository.findUtilisateurByEmail(email)
                .map(UtilisateurDto::fromEntity).orElseThrow(() -> new EntityNotFoundException(
                        "Aucun Utilisateur trouver avec l'email  = " + email + " dans la BDD",
                        ErrorsCode.UTILISATEUR_NOT_FOUND));
    }

    @Override
    public List<UtilisateurDto> findAll() {
        return utilisateurRepository.findAll().stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto) {
        validate(dto);
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(dto.getId());
        if (!utilisateurOptional.isPresent()) {
            log.warn("Aucun Utilisateur trouver avec l'ID  = " + dto.getId() + " dans la BDD");
            throw new EntityNotFoundException(
                    "Aucun Utilisateur trouver avec l'ID  = " + dto.getId() + " dans la BDD",
                    ErrorsCode.UTILISATEUR_NOT_FOUND);
        }
        Utilisateur utilisateur = utilisateurOptional.get();
        utilisateur.setMotDePasse(generateEncodedPassword(dto.getMotDePasse()));

        return UtilisateurDto.fromEntity(
                utilisateurRepository.save(utilisateur));
    }

    @Override
    public void delete(Integer id) {
        if (id == null) {
            log.error("Utilisateur ID is null");
            return;
        }
        Optional<Utilisateur> optional = utilisateurRepository.findById(id);
        Utilisateur utilisateur = new Utilisateur();
        if (optional.isPresent()) {
            utilisateur = optional.get();
        }
        utilisateur.getRoles().forEach(r -> {
            roleRepository.deleteById(r.getId());
        });
        utilisateurRepository.deleteById(id);
    }

    private static void validate(ChangerMotDePasseUtilisateurDto dto) {
        if (dto == null) {
            log.warn("Impossible de changer le mot de passe avec un objet null");
            throw new InvalidOpperatioException("Aucune information n'a ete fourni pour le changgemet de mot de passe",
                    ErrorsCode.UTILISATEUR_CHANGE_PASSWORD_NOT_VALID);
        }
        if (dto.getId() == null) {
            log.warn("Impossible de changer le mot de passe avec un ID null");
            throw new InvalidOpperatioException("ID utilisateur null :: impossible de modifier le mot de passe",
                    ErrorsCode.UTILISATEUR_CHANGE_PASSWORD_NOT_VALID);
        }
        if (!StringUtils.hasLength(dto.getMotDePasse()) || !StringUtils.hasLength(dto.getConfirmMotDePasse())) {
            log.warn("Impossible de changer le mot de passe avec un mot de passe null");
            throw new InvalidOpperatioException(
                    "Mot de passe utilisateur null :: impossible de modifier le mot de passe",
                    ErrorsCode.UTILISATEUR_CHANGE_PASSWORD_NOT_VALID);
        }
        if (!dto.getMotDePasse().equals(dto.getConfirmMotDePasse())) {
            log.warn("Impossible de changer le mot de passe avec deux mots de passw different");
            throw new InvalidOpperatioException(
                    "Mot de passe utilisateur non coforme :: impossible de modifier le mot de passe",
                    ErrorsCode.UTILISATEUR_CHANGE_PASSWORD_NOT_VALID);
        }
    }

    private String generateEncodedPassword(String motDePasse) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(motDePasse);
    }
}
