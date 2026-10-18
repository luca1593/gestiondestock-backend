package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.ChangerMotDePasseUtilisateurDto;
import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Role;
import com.devtech.gestiondestock.model.Utilisateur;
import com.devtech.gestiondestock.repository.EntrepriseRepository;
import com.devtech.gestiondestock.repository.RoleRepository;
import com.devtech.gestiondestock.repository.UtilisateurRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link UtilisateurServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EntrepriseRepository entrepriseRepository;

    @InjectMocks
    private UtilisateurServiceImpl utilisateurService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        MDC.put("idEntreprise", "1");

        utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setNom("Doe");
        utilisateur.setPrenom("John");
        utilisateur.setEmail("john@test.com");
        utilisateur.setMotDePasse("$2a$10$hashExistant");
        utilisateur.setDateDeNaissance(Instant.now());
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    private AdresseDto adresseValide() {
        return AdresseDto.builder()
                .adresse1("1 rue test")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private UtilisateurDto dtoValide() {
        return UtilisateurDto.builder()
                .nom("Doe")
                .prenom("John")
                .email("john@test.com")
                .dateDeNaissance(Instant.now())
                .motDePasse("secret")
                .adresse(adresseValide())
                .entreprise(EntrepriseDto.builder().id(1).build())
                .build();
    }

    // ---------------------------------------------------------------------
    // findById / findByIdWithPassword
    // ---------------------------------------------------------------------

    @Test
    void findById_quandIdNull_retourneNull() {
        assertNull(utilisateurService.findById(null));
        verify(utilisateurRepository, never()).findById(any());
    }

    @Test
    void findById_quandIdExistant_retourneDtoSansMotDePasse() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        UtilisateurDto result = utilisateurService.findById(1);

        assertNotNull(result);
        assertEquals("john@test.com", result.getEmail());
        assertNull(result.getMotDePasse());
    }

    @Test
    void findByIdWithPassword_quandIdExistant_retourneMotDePasse() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        UtilisateurDto result = utilisateurService.findByIdWithPassword(1);

        assertNotNull(result);
        assertEquals("$2a$10$hashExistant", result.getMotDePasse());
    }

    /** Verrouille le correctif : id inexistant => EntityNotFoundException. */
    @Test
    void findById_quandIdInexistant_leveEntityNotFoundException() {
        when(utilisateurRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> utilisateurService.findById(999));
    }

    // ---------------------------------------------------------------------
    // findByNomUtilisateur
    // ---------------------------------------------------------------------

    @Test
    void findByNomUtilisateur_quandNomVide_retourneNull() {
        assertNull(utilisateurService.findByNomUtilisateur(""));
        assertNull(utilisateurService.findByNomUtilisateur(null));
        verify(utilisateurRepository, never()).findUtilisateurByNom(any());
    }

    @Test
    void findByNomUtilisateur_quandExistant_retourneDtoSansMotDePasse() {
        when(utilisateurRepository.findUtilisateurByNom("Doe")).thenReturn(Optional.of(utilisateur));

        UtilisateurDto result = utilisateurService.findByNomUtilisateur("Doe");

        assertNotNull(result);
        assertNull(result.getMotDePasse());
    }

    /** Verrouille le correctif : nom inexistant => EntityNotFoundException. */
    @Test
    void findByNomUtilisateur_quandInexistant_leveEntityNotFoundException() {
        when(utilisateurRepository.findUtilisateurByNom("Inconnu")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> utilisateurService.findByNomUtilisateur("Inconnu"));
    }

    // ---------------------------------------------------------------------
    // findByEmailUtilisateur / findByEmailForAuthentication
    // ---------------------------------------------------------------------

    @Test
    void findByEmailUtilisateur_quandEmailInexistant_leveEntityNotFoundException() {
        when(utilisateurRepository.findUtilisateurByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> utilisateurService.findByEmailUtilisateur("x@x.com"));
    }

    @Test
    void findByEmailForAuthentication_retourneMotDePasse() {
        when(utilisateurRepository.findUtilisateurByEmail("john@test.com"))
                .thenReturn(Optional.of(utilisateur));

        UtilisateurDto result = utilisateurService.findByEmailForAuthentication("john@test.com");

        assertNotNull(result);
        assertEquals("$2a$10$hashExistant", result.getMotDePasse());
    }

    // ---------------------------------------------------------------------
    // save
    // ---------------------------------------------------------------------

    @Test
    void save_quandDtoInvalide_leveInvalidEntityException_etRepositoryJamaisAppele() {
        UtilisateurDto dto = UtilisateurDto.builder().build();

        assertThrows(InvalidEntityException.class, () -> utilisateurService.save(dto));

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void save_quandNouveauSansMotDePasse_leveInvalidEntityException() {
        UtilisateurDto dto = dtoValide();
        dto.setMotDePasse(null);

        assertThrows(InvalidEntityException.class, () -> utilisateurService.save(dto));

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void save_quandNouveau_avecMotDePasse_encodeLeMotDePasse() {
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(inv -> inv.getArgument(0));

        UtilisateurDto result = utilisateurService.save(dtoValide());

        assertNotNull(result.getMotDePasse());
        assertTrue(result.getMotDePasse().startsWith("$2"), "Le mot de passe doit être encodé en BCrypt");
        assertNotEquals("secret", result.getMotDePasse());
    }

    @Test
    void save_quandIdExistant_emailModifie_leveInvalidEntityException_etRepositoryJamaisAppele() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        UtilisateurDto dto = dtoValide();
        dto.setId(1);
        dto.setEmail("autre@test.com");

        assertThrows(InvalidEntityException.class, () -> utilisateurService.save(dto));

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void save_quandIdExistant_motDePasseVide_conserveAncienMotDePasse() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(inv -> inv.getArgument(0));

        UtilisateurDto dto = dtoValide();
        dto.setId(1);
        dto.setMotDePasse(null);

        UtilisateurDto result = utilisateurService.save(dto);

        assertEquals("$2a$10$hashExistant", result.getMotDePasse());
    }

    @Test
    void save_quandIdExistant_motDePasseDejaEncode_conserveLeHash() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(inv -> inv.getArgument(0));

        UtilisateurDto dto = dtoValide();
        dto.setId(1);
        dto.setMotDePasse("$2a$10$nouveauHashDejaEncode");

        UtilisateurDto result = utilisateurService.save(dto);

        assertEquals("$2a$10$nouveauHashDejaEncode", result.getMotDePasse());
    }

    @Test
    void save_quandNouveauEtEntrepriseNull_assigneEntrepriseDuMDC() {
        UtilisateurDto dto = dtoValide();
        dto.setEntreprise(null);

        com.devtech.gestiondestock.model.Entreprise entreprise = new com.devtech.gestiondestock.model.Entreprise();
        entreprise.setId(1);
        when(entrepriseRepository.findById(1)).thenReturn(Optional.of(entreprise));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(inv -> inv.getArgument(0));

        utilisateurService.save(dto);

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(captor.capture());
        assertNotNull(captor.getValue().getEntreprise());
        assertEquals(1, captor.getValue().getEntreprise().getId());
    }

    // ---------------------------------------------------------------------
    // changerMotDePasse
    // ---------------------------------------------------------------------

    @Test
    void changerMotDePasse_quandDtoNull_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class, () -> utilisateurService.changerMotDePasse(null));
    }

    @Test
    void changerMotDePasse_quandIdNull_leveInvalidOpperatioException() {
        ChangerMotDePasseUtilisateurDto dto = ChangerMotDePasseUtilisateurDto.builder()
                .motDePasse("a").confirmMotDePasse("a").build();

        assertThrows(InvalidOpperatioException.class, () -> utilisateurService.changerMotDePasse(dto));
    }

    @Test
    void changerMotDePasse_quandMotsDePasseDifferents_leveInvalidOpperatioException() {
        ChangerMotDePasseUtilisateurDto dto = ChangerMotDePasseUtilisateurDto.builder()
                .id(1).motDePasse("a").confirmMotDePasse("b").build();

        assertThrows(InvalidOpperatioException.class, () -> utilisateurService.changerMotDePasse(dto));
        verify(utilisateurRepository, never()).findById(any());
    }

    @Test
    void changerMotDePasse_quandUtilisateurInexistant_leveEntityNotFoundException() {
        ChangerMotDePasseUtilisateurDto dto = ChangerMotDePasseUtilisateurDto.builder()
                .id(999).motDePasse("a").confirmMotDePasse("a").build();
        when(utilisateurRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> utilisateurService.changerMotDePasse(dto));
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void changerMotDePasse_quandValide_encodeLeMotDePasse() {
        ChangerMotDePasseUtilisateurDto dto = ChangerMotDePasseUtilisateurDto.builder()
                .id(1).motDePasse("nouveau").confirmMotDePasse("nouveau").build();
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(inv -> inv.getArgument(0));

        UtilisateurDto result = utilisateurService.changerMotDePasse(dto);

        assertNotNull(result.getMotDePasse());
        assertTrue(result.getMotDePasse().startsWith("$2"));
        assertNotEquals("nouveau", result.getMotDePasse());
    }

    // ---------------------------------------------------------------------
    // findAll / delete
    // ---------------------------------------------------------------------

    @Test
    void findAll_retourneDtosSansMotDePasse() {
        when(utilisateurRepository.findAll()).thenReturn(Arrays.asList(utilisateur));

        List<UtilisateurDto> result = utilisateurService.findAll();

        assertEquals(1, result.size());
        assertNull(result.get(0).getMotDePasse());
    }

    @Test
    void delete_quandIdNull_neFaitRien() {
        utilisateurService.delete(null);

        verify(utilisateurRepository, never()).findById(any());
        verify(utilisateurRepository, never()).deleteById(any());
        verify(roleRepository, never()).deleteById(any());
    }

    @Test
    void delete_supprimeLesRolesPuisLUtilisateur() {
        Role roleAdmin = new Role();
        roleAdmin.setId(10);
        roleAdmin.setRoleNom("Admin");
        Role roleUser = new Role();
        roleUser.setId(11);
        roleUser.setRoleNom("User");
        utilisateur.setRoles(Arrays.asList(roleAdmin, roleUser));

        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        utilisateurService.delete(1);

        verify(roleRepository).deleteById(10);
        verify(roleRepository).deleteById(11);
        verify(utilisateurRepository).deleteById(1);
    }

    @Test
    void delete_quandUtilisateurInexistant_neLevePasDeNpeEtNeSupprimeRien() {
        when(utilisateurRepository.findById(99)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> utilisateurService.delete(99));

        verify(utilisateurRepository, never()).deleteById(any());
        verify(roleRepository, never()).deleteById(any());
    }

    @Test
    void delete_quandUtilisateurSansRoles_neLevePasDeNpe() {
        utilisateur.setRoles(null);
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        assertDoesNotThrow(() -> utilisateurService.delete(1));

        verify(utilisateurRepository).deleteById(1);
        verify(roleRepository, never()).deleteById(any());
    }
}
