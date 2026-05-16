package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.UtilisateurDto;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationService {

    private final UtilisateurService utilisateurService;

    public AuthorizationService(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    public boolean isAdmin() {
        return hasRole("Admin") || hasRole("ROLE_Admin") || hasRole("ADMIN");
    }

    public boolean isManager() {
        return hasRole("Manager") || hasRole("ROLE_Manager") || hasRole("MANAGER");
    }

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return auth.getName();
    }

    public Integer getCurrentEntrepriseId() {
        String idEntreprise = MDC.get("idEntreprise");
        if (idEntreprise == null) return null;
        try {
            return Integer.parseInt(idEntreprise);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean canAccessUser(Integer targetUserId) {
        if (isAdmin()) return true;

        String currentEmail = getCurrentUserEmail();
        if (currentEmail == null) return false;

        UtilisateurDto currentUser = utilisateurService.findByEmailUtilisateur(currentEmail);
        return currentUser != null && currentUser.getId().equals(targetUserId);
    }

    public boolean canModifyUser(Integer targetUserId) {
        if (isAdmin()) return true;

        String currentEmail = getCurrentUserEmail();
        if (currentEmail == null) return false;

        UtilisateurDto currentUser = utilisateurService.findByEmailUtilisateur(currentEmail);
        return currentUser != null && currentUser.getId().equals(targetUserId);
    }

    public boolean canAccessEntreprise(Integer entrepriseId) {
        if (isAdmin()) return true;

        Integer currentEntrepriseId = getCurrentEntrepriseId();
        return currentEntrepriseId != null && currentEntrepriseId.equals(entrepriseId);
    }

    public boolean hasAnyRole(List<String> roles) {
        return roles.stream().anyMatch(this::hasRole);
    }
}
