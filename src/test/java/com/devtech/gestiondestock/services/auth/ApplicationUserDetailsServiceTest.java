package com.devtech.gestiondestock.services.auth;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.dto.RoleDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.model.auth.ExtendedUser;
import com.devtech.gestiondestock.services.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires (Mockito) de {@link ApplicationUserDetailsService}.
 *
 * <p>On vérifie que toute anomalie produit un {@link UsernameNotFoundException}
 * (converti en 400 BAD_CREDENTIALS par Spring Security) et jamais une
 * {@code NullPointerException} (qui remonterait en HTTP 500).</p>
 */
@ExtendWith(MockitoExtension.class)
class ApplicationUserDetailsServiceTest {

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private ApplicationUserDetailsService service;

    private UtilisateurDto utilisateurValide() {
        return UtilisateurDto.builder()
                .email("admin@test.com")
                .motDePasse("$2a$10$hash")
                .entreprise(EntrepriseDto.builder().id(7).build())
                .roles(Collections.singletonList(RoleDto.builder().id(1).roleNom("Admin").build()))
                .build();
    }

    @Test
    void loadUserByUsername_retourneExtendedUserAvecEntrepriseEtRoles() {
        when(utilisateurService.findByEmailForAuthentication("admin@test.com"))
                .thenReturn(utilisateurValide());

        UserDetails result = service.loadUserByUsername("admin@test.com");

        assertInstanceOf(ExtendedUser.class, result);
        ExtendedUser extendedUser = (ExtendedUser) result;
        assertEquals("admin@test.com", extendedUser.getUsername());
        assertEquals(7, extendedUser.getIdEntreprise());
        assertEquals(1, extendedUser.getAuthorities().size());
        assertEquals("Admin", extendedUser.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_emailInconnu_leveUsernameNotFoundException() {
        when(utilisateurService.findByEmailForAuthentication("inconnu@test.com"))
                .thenThrow(new EntityNotFoundException("Utilisateur introuvable", ErrorsCode.UTILISATEUR_NOT_FOUND));

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("inconnu@test.com"));
    }

    @Test
    void loadUserByUsername_emailVide_leveUsernameNotFoundExceptionSansNpe() {
        when(utilisateurService.findByEmailForAuthentication("")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(""));
    }

    @Test
    void loadUserByUsername_sansEntreprise_leveUsernameNotFoundExceptionSansNpe() {
        UtilisateurDto dto = utilisateurValide();
        dto.setEntreprise(null);
        when(utilisateurService.findByEmailForAuthentication("admin@test.com")).thenReturn(dto);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("admin@test.com"));
    }

    @Test
    void loadUserByUsername_entrepriseSansId_leveUsernameNotFoundException() {
        UtilisateurDto dto = utilisateurValide();
        dto.setEntreprise(EntrepriseDto.builder().build());
        when(utilisateurService.findByEmailForAuthentication("admin@test.com")).thenReturn(dto);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("admin@test.com"));
    }

    @Test
    void loadUserByUsername_sansRoles_retourneUneListeDeRolesVide() {
        UtilisateurDto dto = utilisateurValide();
        dto.setRoles(null);
        when(utilisateurService.findByEmailForAuthentication("admin@test.com")).thenReturn(dto);

        UserDetails result = service.loadUserByUsername("admin@test.com");

        assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_sansMotDePasse_leveUsernameNotFoundException() {
        UtilisateurDto dto = utilisateurValide();
        dto.setMotDePasse(null);
        when(utilisateurService.findByEmailForAuthentication("admin@test.com")).thenReturn(dto);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("admin@test.com"));
    }
}
