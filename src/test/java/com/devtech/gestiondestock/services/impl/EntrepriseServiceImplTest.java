package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Role;
import com.devtech.gestiondestock.repository.EntrepriseRepository;
import com.devtech.gestiondestock.repository.RoleRepository;
import com.devtech.gestiondestock.services.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link EntrepriseServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class EntrepriseServiceImplTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;
    @Mock
    private UtilisateurService utilisateurService;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private EntrepriseServiceImpl entrepriseService;

    private Entreprise entreprise;

    @BeforeEach
    void setUp() {
        entreprise = new Entreprise();
        entreprise.setId(1);
        entreprise.setNom("Tech Corp");
        entreprise.setDescription("Société de test");
        entreprise.setEmail("contact@tech.com");
    }

    private AdresseDto adresseValide() {
        return AdresseDto.builder()
                .adresse1("1 rue test")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private EntrepriseDto dtoValide() {
        return EntrepriseDto.builder()
                .nom("Tech Corp")
                .description("Société de test")
                .email("contact@tech.com")
                .codeFiscal("FISCAL123")
                .numTel("0102030405")
                .adresse(adresseValide())
                .build();
    }

    // ---------------------------------------------------------------------
    // findById / findbyNomEntreprise / findbyEmailEntreprise
    // ---------------------------------------------------------------------

    @Test
    void findById_quandIdNull_retourneNull() {
        assertNull(entrepriseService.findById(null));
        verify(entrepriseRepository, never()).findById(any());
    }

    @Test
    void findById_quandIdExistant_retourneDto() {
        when(entrepriseRepository.findById(1)).thenReturn(Optional.of(entreprise));

        EntrepriseDto result = entrepriseService.findById(1);

        assertNotNull(result);
        assertEquals("Tech Corp", result.getNom());
    }

    /** Verrouille le correctif : id inexistant => EntityNotFoundException. */
    @Test
    void findById_quandIdInexistant_leveEntityNotFoundException() {
        when(entrepriseRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> entrepriseService.findById(999));
    }

    @Test
    void findbyNomEntreprise_quandNomVide_retourneNull() {
        assertNull(entrepriseService.findbyNomEntreprise(""));
        assertNull(entrepriseService.findbyNomEntreprise(null));
        verify(entrepriseRepository, never()).findEntrepriseByNom(any());
    }

    /** Verrouille le correctif : nom inexistant => EntityNotFoundException. */
    @Test
    void findbyNomEntreprise_quandInexistant_leveEntityNotFoundException() {
        when(entrepriseRepository.findEntrepriseByNom("Inconnu")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> entrepriseService.findbyNomEntreprise("Inconnu"));
    }

    /** Verrouille le correctif : email inexistant => EntityNotFoundException. */
    @Test
    void findbyEmailEntreprise_quandInexistant_leveEntityNotFoundException() {
        when(entrepriseRepository.findEntrepriseByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> entrepriseService.findbyEmailEntreprise("x@x.com"));
    }

    @Test
    void findbyEmailEntreprise_quandExistant_retourneDto() {
        when(entrepriseRepository.findEntrepriseByEmail("contact@tech.com"))
                .thenReturn(Optional.of(entreprise));

        EntrepriseDto result = entrepriseService.findbyEmailEntreprise("contact@tech.com");

        assertNotNull(result);
        assertEquals("contact@tech.com", result.getEmail());
    }

    // ---------------------------------------------------------------------
    // save
    // ---------------------------------------------------------------------

    @Test
    void save_quandDtoInvalide_leveInvalidEntityException_etRepositoryJamaisAppele() {
        assertThrows(InvalidEntityException.class, () -> entrepriseService.save(EntrepriseDto.builder().build()));

        verify(entrepriseRepository, never()).save(any());
    }

    @Test
    void save_quandNouvelleEntreprise_creeUtilisateurEtRoleAdmin() {
        when(entrepriseRepository.save(any(Entreprise.class))).thenAnswer(inv -> {
            Entreprise saved = inv.getArgument(0);
            saved.setId(1);
            return saved;
        });
        when(utilisateurService.save(any(UtilisateurDto.class)))
                .thenReturn(UtilisateurDto.builder().id(2).build());

        EntrepriseDto result = entrepriseService.save(dtoValide());

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(utilisateurService, times(1)).save(any(UtilisateurDto.class));

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository, times(1)).save(roleCaptor.capture());
        assertEquals("Admin", roleCaptor.getValue().getRoleNom());
    }

    @Test
    void save_quandEntrepriseExistante_neCreePasUtilisateurNiRole() {
        when(entrepriseRepository.save(any(Entreprise.class))).thenAnswer(inv -> inv.getArgument(0));

        EntrepriseDto dto = dtoValide();
        dto.setId(1);

        entrepriseService.save(dto);

        verify(utilisateurService, never()).save(any());
        verify(roleRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // findAll / delete / updatePhoto
    // ---------------------------------------------------------------------

    @Test
    void findAll_retourneListe() {
        when(entrepriseRepository.findAll()).thenReturn(Arrays.asList(entreprise));

        var result = entrepriseService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void delete_appelleRepository() {
        entrepriseService.delete(1);

        verify(entrepriseRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_quandIdNull_neFaitRien() {
        entrepriseService.delete(null);

        verify(entrepriseRepository, never()).deleteById(any());
    }

    @Test
    void updatePhoto_quandInexistant_leveEntityNotFoundException() {
        when(entrepriseRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> entrepriseService.updatePhoto(999, "http://photo"));
        verify(entrepriseRepository, never()).save(any());
    }

    @Test
    void updatePhoto_quandExistant_metAJourLaPhoto() {
        when(entrepriseRepository.findById(1)).thenReturn(Optional.of(entreprise));
        when(entrepriseRepository.save(any(Entreprise.class))).thenAnswer(inv -> inv.getArgument(0));

        EntrepriseDto result = entrepriseService.updatePhoto(1, "http://nouvelle-photo");

        assertEquals("http://nouvelle-photo", result.getPhoto());
        verify(entrepriseRepository).save(entreprise);
    }
}
