package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.UtilisateurApi;
import com.devtech.gestiondestock.dto.ChangerMotDePasseUtilisateurDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.services.AuthorizationService;
import com.devtech.gestiondestock.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtilisateurController implements UtilisateurApi {

    private final UtilisateurService utilisateurService;
    private final AuthorizationService authorizationService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService, AuthorizationService authorizationService) {
        this.utilisateurService = utilisateurService;
        this.authorizationService = authorizationService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public UtilisateurDto save(UtilisateurDto dto) {
        return this.utilisateurService.save(dto);
    }

    @Override
    public UtilisateurDto findById(Integer id) {
        if (!authorizationService.canAccessUser(id)) {
            throw new AccessDeniedException("Vous n'avez pas le droit d'accéder à cet utilisateur");
        }
        return this.utilisateurService.findById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public UtilisateurDto findByNomUtilisateur(String nom) {
        return this.utilisateurService.findByNomUtilisateur(nom);
    }

    @Override
    public UtilisateurDto findByEmailUtilisateur(String email) {
        if (!authorizationService.isAdmin() && !authorizationService.getCurrentUserEmail().equals(email)) {
            throw new AccessDeniedException("Vous n'avez pas le droit d'accéder à cet utilisateur");
        }
        return this.utilisateurService.findByEmailUtilisateur(email);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public List<UtilisateurDto> findAll() {
        return this.utilisateurService.findAll();
    }

    @Override
    public UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto) {
        if (!authorizationService.canModifyUser(dto.getId())) {
            throw new AccessDeniedException("Vous ne pouvez modifier que votre propre mot de passe");
        }
        return this.utilisateurService.changerMotDePasse(dto);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN')")
    public void delete(Integer id) {
        this.utilisateurService.delete(id);
    }

}
